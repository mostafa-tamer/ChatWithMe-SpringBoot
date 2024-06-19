package com.mostafatamer.trysomethingcrazy.service;

import com.mostafatamer.trysomethingcrazy.domain.dto.chat.ChatMessageDto;
import com.mostafatamer.trysomethingcrazy.domain.entity.ChatEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.ChatMessageEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.UserEntity;
import com.mostafatamer.trysomethingcrazy.exceptions.ClientException;
import com.mostafatamer.trysomethingcrazy.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatEntity save(ChatEntity chatEntity) {
        chatEntity.setTag(UUID.randomUUID().toString());
        return chatRepository.save(chatEntity);
    }

    public List<ChatEntity> getChats(UserEntity user) {
        List<UserEntity> userFriends = user.getFriends();
        List<ChatEntity> chats = new ArrayList<>();

        for (UserEntity userFriend : userFriends) {
            ChatEntity chat = chatRepository.findChatByUsers(userFriend , user)
                    .orElseThrow(() -> new IllegalArgumentException("chat not found"));
            chats.add(chat);
        }

        return chats;
    }

    public ChatEntity findByChatTag(String chatTag) {
        return chatRepository.findByTag(chatTag).orElseThrow(() -> new ClientException("chat not found"));
    }

    public Optional<ChatEntity> findOptionalByChatTag(String chatTag) {
        return chatRepository.findByTag(chatTag);
    }

    public ChatMessageEntity findChatLastMessage(ChatEntity chatEntity) {
        return chatRepository.findLastMessage(chatEntity).orElse(null);
    }

    public List<ChatEntity> getAllChats(UserEntity user) {
        return chatRepository.findByUser(user);
    }

    public Page<ChatEntity> getAllChats(UserEntity user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return chatRepository.findByUser(user, pageable);
    }

    public ChatMessageDto entityToDtoConverter(ChatMessageEntity chatMessageEntity) {
        return ChatMessageDto.builder()
                .senderUsername(chatMessageEntity.getSenderUsername())
                .message(chatMessageEntity.getMessage())
                .timeStamp(chatMessageEntity.getTimeStamp().toEpochSecond(ZoneOffset.UTC))
                .messageNumber(chatMessageEntity.getMessageNumber())
                .build();
    }


//    public List<ChatMessageEntity> findChatLastMessage(ChatEntity chatEntity) {
//        return chatRepository.findLastMessage(chatEntity) ;
//    }
}
