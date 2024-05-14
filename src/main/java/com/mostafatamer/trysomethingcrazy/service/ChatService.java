package com.mostafatamer.trysomethingcrazy.service;

import com.mostafatamer.trysomethingcrazy.domain.entity.ChatEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.UserEntity;
import com.mostafatamer.trysomethingcrazy.exceptions.ClientException;
import com.mostafatamer.trysomethingcrazy.repository.ChatRepository;
import com.mostafatamer.trysomethingcrazy.repository.FriendshipRepository;
import com.mostafatamer.trysomethingcrazy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
