package com.mostafatamer.trysomethingcrazy.controller;

import com.mostafatamer.trysomethingcrazy.constants.MessageBrokers;
import com.mostafatamer.trysomethingcrazy.domain.ApiResponse;
import com.mostafatamer.trysomethingcrazy.domain.dto.chat.ChatDto;
import com.mostafatamer.trysomethingcrazy.domain.dto.chat.ChatLastMessage;
import com.mostafatamer.trysomethingcrazy.domain.dto.chat.ChatMessageDto;
import com.mostafatamer.trysomethingcrazy.domain.entity.ChatEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.ChatMessageEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.UserEntity;
import com.mostafatamer.trysomethingcrazy.domain.enumeration.MessageType;
import com.mostafatamer.trysomethingcrazy.domain.firebase.ChatMessage;
import com.mostafatamer.trysomethingcrazy.domain.firebase.CloudMessage;
import com.mostafatamer.trysomethingcrazy.mappers.impl.UserMapper;
import com.mostafatamer.trysomethingcrazy.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Log
@Controller
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    private final UserService userService;
    private final UserMapper userMapper;

    private final SimpMessagingTemplate messagingTemplate;

    private final MessagesService messagesService;

    private final FirebaseMessagingService firebaseMessagingService;
    private final FriendshipService friendshipService;

    @MessageMapping("/sendMessage")
    void sendMessageToFriend(@Payload @Valid ChatMessageDto chatMessageDto) {
        System.out.println(chatMessageDto);

        ChatEntity chatEntity = chatService.findByChatTag(chatMessageDto.getChatTag());

        ChatMessageEntity savedMessage = saveMessage(chatMessageDto, chatEntity);

        modifyTheMessageDto(chatMessageDto, savedMessage);
        System.out.println("firebase");

        sendFirebaseMessage(chatMessageDto, chatEntity, savedMessage);

        webSocketNewMessageNotification(chatMessageDto, chatEntity);
    }

    private void modifyTheMessageDto(ChatMessageDto chatMessageDto, ChatMessageEntity savedMessage) {
        chatMessageDto.setTimeStamp(savedMessage.getTimeStamp().toEpochSecond(ZoneOffset.UTC));
        chatMessageDto.setMessageNumber(savedMessage.getMessageNumber());
    }

    private void webSocketNewMessageNotification(ChatMessageDto chatMessageDto, ChatEntity chatEntity) {
        System.out.println("webSocketNewMessageNotification");

        String destinationForPrivateChat = MessageBrokers.SEND_MESSAGE_TO_CHAT + "/" + chatMessageDto.getChatTag();
        messagingTemplate.convertAndSend(destinationForPrivateChat, chatMessageDto);
        System.out.println(destinationForPrivateChat);

        for (UserEntity user : chatEntity.getMembers()) {
            String destinationForFriendshipChatHub = MessageBrokers.SEND_MESSAGE_TO_CHAT + "/" + user.getUsername();
            messagingTemplate.convertAndSend(destinationForFriendshipChatHub, chatMessageDto);
        }
    }

    private void sendFirebaseMessage(ChatMessageDto chatMessageDto, ChatEntity chatEntity, ChatMessageEntity savedMessage) {
        UserEntity sender = userService.findByUsername(chatMessageDto.getSenderUsername());

        String receiverUsername = getOtherUser(chatEntity, sender).getUsername();

        UserEntity receiver = userService.findByUsername(receiverUsername);

        firebaseMessagingService.sendClientMessage(
                receiver.getFirebaseToken(), CloudMessage.builder()
                        .messageType(MessageType.FRIEND_CHAT_MESSAGE)
                        .data(ChatMessage.builder()
                                .title(sender.getNickname())
                                .chatDto(ChatDto.builder()
                                        .tag(chatEntity.getTag())
                                        .members(chatEntity.getMembers().stream()
                                                .map(user -> userMapper.entityToDto(sender))
                                                .toList())
                                        .build())
                                .message(savedMessage.getMessage()))
                        .build()
        );

        log.info(chatMessageDto.getSenderUsername() + " " + chatMessageDto.getMessage());
    }

    private ChatMessageEntity saveMessage(ChatMessageDto chatMessageDto, ChatEntity chatEntity) {
        ChatMessageEntity message = ChatMessageEntity.builder()
                .senderUsername(chatMessageDto.getSenderUsername())
                .message(chatMessageDto.getMessage())
                .chat(chatEntity)
                .build();

        return messagesService.save(message);
    }

    @GetMapping("/messages")
    ApiResponse<List<ChatMessageDto>> messages(@DestinationVariable String chatTag) {
        ChatEntity chatEntity = chatService.findByChatTag(chatTag);
        List<ChatMessageEntity> chatMessages = messagesService.findByChatId(chatEntity.getId());


        return ApiResponse.<List<ChatMessageDto>>builder()
                .data(chatMessages.stream()
                        .map(chatMessageEntity -> ChatMessageDto.builder()
                                .senderUsername(chatMessageEntity.getSenderUsername()).message(chatMessageEntity.getMessage())
                                .timeStamp(chatMessageEntity.getTimeStamp().atOffset(ZoneOffset.UTC).toEpochSecond())
                                .messageNumber(chatMessageEntity.getMessageNumber())
                                .build())
                        .toList())
                .build();
    }

    @SneakyThrows
    @GetMapping("/chats")
    ApiResponse<Page<ChatDto>> chats(@DestinationVariable Integer page, @DestinationVariable Integer size) {
        Thread.sleep(1500);
        UserEntity request = userService.findByUsername(AuthenticationService.getUserEntity().getUsername());

        Page<ChatEntity> chats = chatService.getAllChats(request, page, size);

        Page<ChatDto> chatResponse = chats
                .map(chat -> {
                    var members = chat.getMembers().stream()
                            .map(userMapper::entityToDto)
                            .toList();

                    var chatWithLastMessage = chatService.findChatLastMessage(chat);

                    ChatMessageDto lastMessage = chatWithLastMessage != null ?
                            chatService.entityToDtoConverter(chatWithLastMessage) : null;

                    return ChatDto.builder()
                            .tag(chat.getTag())
                            .members(members)
                            .lastMessage(lastMessage)
                            .build();
                });

        return ApiResponse.<Page<ChatDto>>builder()
                .data(chatResponse)
                .build();
    }

    private UserEntity getOtherUser(ChatEntity chatEntity, UserEntity user) {
        return chatEntity.getMembers().stream()
                .filter(userEntity -> !userEntity.getUsername().equals(user.getUsername()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("other not found"));
    }

    @GetMapping("/messageNumbers")
    ApiResponse<List<ChatLastMessage>> getLastMessageNumbers(@RequestParam List<String> chatTags) {
        //ignore the invalid chat tags
        List<ChatEntity> availableChats = chatTags.stream()
                .map(chatService::findOptionalByChatTag)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        return ApiResponse.<List<ChatLastMessage>>builder()
                .data(availableChats.stream()
                        .map(chatEntity -> ChatLastMessage.builder()
                                .tag(chatEntity.getTag())
                                .lastMessageNumber(messagesService.findLastMessageNumber(chatEntity))
                                .build())
                        .toList())
                .build();
    }

    @GetMapping("/chatLastMessageNumber")
    ApiResponse<ChatLastMessage> chatLastMessageNumber(@RequestParam String chatTag) {

        ChatEntity chatEntity = chatService.findByChatTag(chatTag);

        return ApiResponse.<ChatLastMessage>builder()
                .data(ChatLastMessage.builder()
                        .tag(chatEntity.getTag())
                        .lastMessageNumber(messagesService.findLastMessageNumber(chatEntity))
                        .build())
                .build();
    }
}
