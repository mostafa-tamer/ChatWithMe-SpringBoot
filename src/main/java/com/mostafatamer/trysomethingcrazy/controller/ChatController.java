package com.mostafatamer.trysomethingcrazy.controller;

import com.mostafatamer.trysomethingcrazy.constants.MessageBrokers;
import com.mostafatamer.trysomethingcrazy.domain.ApiResponse;
import com.mostafatamer.trysomethingcrazy.domain.dto.UserDto;
import com.mostafatamer.trysomethingcrazy.domain.firebase.ChatMessage;
import com.mostafatamer.trysomethingcrazy.domain.enumeration.MessageType;
import com.mostafatamer.trysomethingcrazy.domain.dto.chat.ChatDto;
import com.mostafatamer.trysomethingcrazy.domain.dto.chat.ChatLastMessage;
import com.mostafatamer.trysomethingcrazy.domain.dto.chat.ChatMessageDto;
import com.mostafatamer.trysomethingcrazy.domain.firebase.CloudMessage;
import com.mostafatamer.trysomethingcrazy.domain.entity.ChatEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.ChatMessageEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.UserEntity;
import com.mostafatamer.trysomethingcrazy.mappers.impl.UserMapper;
import com.mostafatamer.trysomethingcrazy.service.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @MessageMapping("/sendMessage/{chatTag}")
    void sendMessageToFriend(
            @DestinationVariable String chatTag,
            @Payload @Valid ChatMessageDto chatMessageDto) {

        ChatEntity chatEntity = chatService.findByChatTag(chatTag);

        ChatMessageEntity savedMessage = saveMessage(chatMessageDto, chatEntity);

        modifyTheMessageDto(chatMessageDto, savedMessage);

        sendFirebaseMessage(chatMessageDto, chatEntity, savedMessage);

        webSocketNewMessageNotification(chatTag, chatMessageDto);
    }

    private static void modifyTheMessageDto(ChatMessageDto chatMessageDto, ChatMessageEntity savedMessage) {
        chatMessageDto.setTimeStamp(savedMessage.getTimeStamp().toEpochSecond(ZoneOffset.UTC));
        chatMessageDto.setMessageNumber(savedMessage.getMessageNumber());
    }

    private void webSocketNewMessageNotification(String chatTag, ChatMessageDto chatMessageDto) {
        String destination = MessageBrokers.SEND_MESSAGE_TO_CHAT + "/" + chatTag;
        messagingTemplate.convertAndSend(destination, chatMessageDto);
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
                                        .friend(userMapper.entityToDto(sender))
                                        .build())
                                .message(savedMessage.getMessage()))
                        .build()
        );

        log.info(chatMessageDto.getSenderUsername() + " " +
                chatMessageDto.getMessage());
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


    @GetMapping("/chats")
    ApiResponse<List<ChatDto>> chats() {
        UserEntity user = userService.findByUsername(
                AuthenticationService.getUserEntity().getUsername()
        );

        List<ChatEntity> chats = chatService.getChats(user);

        List<ChatDto> chatResponse = chats.stream()
                .map(chatEntity -> {
                    String other = getOtherUser(chatEntity, user).getUsername();

                    UserEntity friendEntity = userService.findByUsername(other);

                    UserDto friend = userMapper.entityToDto(friendEntity);

                    return ChatDto.builder()
                            .tag(chatEntity.getTag())
                            .friend(friend)
                            .build();
                }).toList();

        return ApiResponse.<List<ChatDto>>builder()
                .data(chatResponse)
                .build();
    }

    private UserEntity getOtherUser(ChatEntity chatEntity, UserEntity user) {
        chatEntity.getUsers().stream().map(UserEntity::getUsername).forEach(System.out::println);
        return chatEntity.getUsers().stream()
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
