package com.mostafatamer.trysomethingcrazy.service;

import com.mostafatamer.trysomethingcrazy.domain.entity.ChatEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.ChatMessageEntity;
import com.mostafatamer.trysomethingcrazy.exceptions.ClientException;
import com.mostafatamer.trysomethingcrazy.repository.MessagesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessagesService {

    private final MessagesRepository messagesRepository;

    public ChatMessageEntity save(ChatMessageEntity chatMessageEntity) {

        ChatEntity chatEntity = chatMessageEntity.getChat();
        var largestMessageNumberInChat = messagesRepository.getLargestMessageNumberInChat(chatEntity);

        largestMessageNumberInChat.ifPresentOrElse(
                integer -> chatMessageEntity.setMessageNumber(integer+ 1L),
                () -> chatMessageEntity.setMessageNumber(1L)
        );

        return messagesRepository.save(chatMessageEntity);
    }

    public Long findLastMessageNumber(ChatEntity chat) {
        return messagesRepository.findLastMessageNumber(chat).orElse(0L);
    }
    public Long findLastMessageNumber(String chatTag) {
        return messagesRepository.findLastMessageNumber(chatTag).orElse(0L);
    }

    public List<ChatMessageEntity> findByChatId(Long id) {
        return messagesRepository.findByChatId(id).orElseThrow(() -> new ClientException("messages not found"));
    }
}
