package com.mostafatamer.trysomethingcrazy.controller;

import com.mostafatamer.trysomethingcrazy.constants.MessageBrokers;
import com.mostafatamer.trysomethingcrazy.domain.ApiResponse;
import com.mostafatamer.trysomethingcrazy.domain.dto.UserDto;
import com.mostafatamer.trysomethingcrazy.domain.dto.chat.ChatDto;
import com.mostafatamer.trysomethingcrazy.domain.dto.chat.ChatLastMessage;
import com.mostafatamer.trysomethingcrazy.domain.dto.chat.ChatMessageDto;
import com.mostafatamer.trysomethingcrazy.domain.entity.ChatEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.ChatMessageEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.UserEntity;
import com.mostafatamer.trysomethingcrazy.domain.enumeration.MessageType;
import com.mostafatamer.trysomethingcrazy.domain.firebase.AddToGroup;
import com.mostafatamer.trysomethingcrazy.domain.firebase.ChatMessage;
import com.mostafatamer.trysomethingcrazy.domain.firebase.CloudMessage;
import com.mostafatamer.trysomethingcrazy.exceptions.ClientException;
import com.mostafatamer.trysomethingcrazy.mappers.impl.UserMapper;
import com.mostafatamer.trysomethingcrazy.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
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
    private final UserMapper userMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final UserService userService;
    private final MessagesService messagesService;
    private final FirebaseMessagingService firebaseMessagingService;

    @MessageMapping("/sendMessage")
    void sendMessageToFriend(@Payload @Valid ChatMessageDto chatMessageDto) {
        System.out.println(chatMessageDto);

        ChatEntity chatEntity = chatService.findByChatTag(chatMessageDto.getChatTag());

        ChatMessageEntity savedMessage = saveMessage(chatMessageDto, chatEntity);

        modifyTheMessageDto(chatMessageDto, savedMessage);

        webSocketNewMessageNotification(chatMessageDto, chatEntity);

        sendFirebaseMessage(chatMessageDto, chatEntity, savedMessage);
    }

    @GetMapping("/messages")
    ApiResponse<Page<ChatMessageDto>> messages(@DestinationVariable String chatTag, @DestinationVariable Integer page, Integer size) throws InterruptedException {
        Thread.sleep(750);

        ChatEntity chatEntity = chatService.findByChatTag(chatTag);
        Page<ChatMessageEntity> chatMessages = messagesService.getPageableMessages(chatEntity, page, size);


        return ApiResponse.<Page<ChatMessageDto>>builder()
                .data(chatMessages.map(chatMessageEntity -> {
                            UserDto sender = userMapper.entityToDto(
                                    userService.findByUsername(chatMessageEntity.getSenderUsername())
                            );
                            return ChatMessageDto.builder()
                                    .chatTag(chatMessageEntity.getChat().getTag())
                                    .sender(sender)
                                    .message(chatMessageEntity.getMessage())
                                    .timeStamp(chatMessageEntity.getTimeStamp().atOffset(ZoneOffset.UTC).toEpochSecond())
                                    .messageNumber(chatMessageEntity.getMessageNumber())
                                    .build();
                        })
                ).build();
    }

    @GetMapping("/groupsChat")
    ApiResponse<Page<ChatDto>> groupsChat(@DestinationVariable Integer page, @DestinationVariable Integer size) throws InterruptedException {
        Thread.sleep(750);

        UserEntity request = userService.findByUsername(AuthenticationService.getUserEntity().getUsername());

        Page<ChatEntity> chats = chatService.getGroupsChat(request, page, size);

        Page<ChatDto> chatResponse = chats.map(this::prepareChatDto);

        return ApiResponse.<Page<ChatDto>>builder()
                .data(chatResponse)
                .build();
    }

    @GetMapping("/chats")
    ApiResponse<Page<ChatDto>> chats(@DestinationVariable Integer page, @DestinationVariable Integer size) throws InterruptedException {
        Thread.sleep(750);
        UserEntity request = userService.findByUsername(AuthenticationService.getUserEntity().getUsername());

        Page<ChatEntity> chats = chatService.getPrivateChats(request, page, size);

        System.out.println("size" + " " + chats.getSize());

        Page<ChatDto> chatResponse = chats.map(chat -> {
            System.out.println(chat);
            return prepareChatDto(chat);
        });

        return ApiResponse.<Page<ChatDto>>builder()
                .data(chatResponse)
                .build();
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

    @PostMapping("/create_group_chat")
    ApiResponse<ChatDto> createGroup(@RequestParam String groupName) {
        UserEntity client = userService.findByUsername(AuthenticationService.getUserEntity().getUsername());

        ChatEntity chatToSave = ChatEntity.builder()
                .isGroupChat(true)
                .chatGroupName(groupName)
                .members(List.of(client))
                .build();

        ChatEntity savedChat = chatService.createChat(chatToSave);

        ChatDto chatDto = ChatDto.builder()
                .members(savedChat.getMembers().stream()
                        .map(userMapper::entityToDto)
                        .toList())
                .tag(savedChat.getTag())
                .groupName(groupName)
                .build();

        return ApiResponse.<ChatDto>builder()
                .data(chatDto)
                .build();
    }

    @PostMapping("/add_friend_to_chat_group")
    ApiResponse<ChatDto> addFriendToChatGroup(@DestinationVariable String friendUsername, @DestinationVariable String chatTag) {
        UserEntity user = userService.findByUsername(AuthenticationService.getUserEntity().getUsername());

        UserEntity friend = userService.findByUsername(friendUsername);

        if (!friend.getFriends().contains(user)) {
            throw new ClientException("You can only add friends to chat group");
        }

        ChatEntity chatToAdd = chatService.findByChatTag(chatTag);

        if (chatToAdd.getMembers().contains(friend)) {
            throw new ClientException(friendUsername + " already in this chat group");
        }

        ChatEntity chatEntity = chatService.addFriendToGroup(chatTag, friend);

        notifyWebSocketListenersTheyAreAddedToGroup(friendUsername, chatEntity);

        notifyFirebaseThatTheUserIsAddedToGroup(friend, CloudMessage.builder()
                .messageType(MessageType.ADD_TO_GROUP)
                .data(AddToGroup.builder()
                        .groupChat(ChatDto.builder()
                                .groupName(chatEntity.getChatGroupName())
                                .tag(chatEntity.getTag())
                                .build())
                        .build()
                ));

        return ApiResponse.<ChatDto>builder()
                .data(ChatDto.builder()
                        .members(chatEntity.getMembers().stream()
                                .map(userMapper::entityToDto)
                                .toList())
                        .tag(chatEntity.getTag())
                        .groupName(chatEntity.getChatGroupName())
                        .build()
                ).build();
    }

    private void notifyFirebaseThatTheUserIsAddedToGroup(UserEntity friend, CloudMessage.CloudMessageBuilder<Object> ADD_TO_GROUP) {
        firebaseMessagingService.sendClientMessage(
                friend.getFirebaseToken(), ADD_TO_GROUP.build()
        );
    }

    @PostMapping("/leave_chat_group")
    ApiResponse<ChatDto> addFriendToChatGroup(@DestinationVariable String chatTag) {
        UserEntity client = userService.findByUsername(AuthenticationService.getUserEntity().getUsername());

        ChatEntity chatEntity = chatService.leaveChatGroup(chatTag, client);

        return ApiResponse.<ChatDto>builder()
                .data(ChatDto.builder()
                        .members(chatEntity.getMembers().stream()
                                .map(userMapper::entityToDto)
                                .toList())
                        .tag(chatEntity.getTag())
                        .groupName(chatEntity.getChatGroupName())
                        .build()
                ).build();
    }

    private void notifyWebSocketListenersTheyAreAddedToGroup(String friendUsername, ChatEntity chatEntity) {
        String destinationForPrivateChat = MessageBrokers.ADD_TO_GROUP + "/" + friendUsername;

        ChatMessageEntity lastMessageEntity = chatService.findChatLastMessage(chatEntity);

        ChatMessageDto chatMessageDto = null;

        if (lastMessageEntity != null) {
            UserDto sender = userMapper.entityToDto(userService.findByUsername(lastMessageEntity.getSenderUsername()));
            chatMessageDto = chatService.chatMessageEntityToDtoConverter(lastMessageEntity, sender);
        }
        messagingTemplate.convertAndSend(
                destinationForPrivateChat, ChatDto.builder()
                        .groupName(chatEntity.getChatGroupName())
                        .tag(chatEntity.getTag())
                        .lastMessage(chatMessageDto)
                        .members(chatEntity.getMembers().stream().map(userMapper::entityToDto).toList())
                        .build()
        );
    }

    private void modifyTheMessageDto(ChatMessageDto chatMessageDto, ChatMessageEntity savedMessage) {
        chatMessageDto.setTimeStamp(savedMessage.getTimeStamp().toEpochSecond(ZoneOffset.UTC));
        chatMessageDto.setMessageNumber(savedMessage.getMessageNumber());
    }

    private void webSocketNewMessageNotification(ChatMessageDto chatMessageDto, ChatEntity chatEntity) {
        String destinationForPrivateChat = MessageBrokers.SEND_MESSAGE_TO_CHAT + "/" + chatMessageDto.getChatTag();
        messagingTemplate.convertAndSend(destinationForPrivateChat, chatMessageDto);

        for (UserEntity user : chatEntity.getMembers()) {
            String destinationForFriendshipChatHub = MessageBrokers.SEND_MESSAGE_TO_CHAT + "/" + user.getUsername();
            messagingTemplate.convertAndSend(destinationForFriendshipChatHub, chatMessageDto);
        }
    }

    private void sendFirebaseMessage(ChatMessageDto chatMessageDto, ChatEntity chatEntity, ChatMessageEntity savedMessage) {
        UserEntity sender = userService.findByUsername(chatMessageDto.getSender().getUsername());

        String receiverUsername = getOtherUser(chatEntity, sender).getUsername();

        UserEntity receiver = userService.findByUsername(receiverUsername);

        var members = chatEntity.getMembers().stream()
                .map(userMapper::entityToDto)
                .toList();


        var chatDto = ChatDto.builder()
                .tag(chatEntity.getTag())
                .members(members)
                .groupName(chatEntity.getChatGroupName())
                .build();

        notifyFirebaseThatTheUserIsAddedToGroup(receiver, CloudMessage.builder()
                .messageType(MessageType.FRIEND_CHAT_MESSAGE)
                .data(ChatMessage.builder()
                        .title(sender.getNickname())
                        .message(savedMessage.getMessage())
                        .chat(chatDto)
                ));

        log.info(chatMessageDto.getSender() + " " + chatMessageDto.getMessage());
    }

    private ChatMessageEntity saveMessage(ChatMessageDto chatMessageDto, ChatEntity chatEntity) {
        ChatMessageEntity message = ChatMessageEntity.builder()
                .senderUsername(chatMessageDto.getSender().getUsername())
                .message(chatMessageDto.getMessage())
                .chat(chatEntity)
                .build();

        return messagesService.save(message);
    }

    private ChatDto prepareChatDto(ChatEntity chat) {
        ChatMessageEntity chatWithLastMessage = chatService.findChatLastMessage(chat);

        ChatMessageDto lastMessage = chatWithLastMessage != null ?
                chatService.chatMessageEntityToDtoConverter(
                        chatWithLastMessage,
                        userMapper.entityToDto(
                                userService.findByUsername(chatWithLastMessage.getSenderUsername())
                        )) : null;

        var members = chat.getMembers().stream()
                .map(userMapper::entityToDto)
                .toList();

        return ChatDto.builder()
                .tag(chat.getTag())
                .groupName(chat.getChatGroupName())
                .members(members)
                .lastMessage(lastMessage)
                .build();
    }

    private UserEntity getOtherUser(ChatEntity chatEntity, UserEntity user) {
        return chatEntity.getMembers().stream()
                .filter(userEntity -> !userEntity.getUsername().equals(user.getUsername()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("other not found"));
    }
}
