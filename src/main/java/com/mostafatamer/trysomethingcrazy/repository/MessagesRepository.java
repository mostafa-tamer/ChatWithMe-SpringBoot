package com.mostafatamer.trysomethingcrazy.repository;

import com.mostafatamer.trysomethingcrazy.domain.entity.ChatEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessagesRepository extends JpaRepository<ChatMessageEntity, Long> {

    Optional<List<ChatMessageEntity>> findByChatId(Long id);

    @Query("""
            SELECT cm
            FROM ChatMessageEntity cm
            WHERE
            cm.timeStamp = (
                SELECT MAX(cm2.timeStamp)
                FROM ChatMessageEntity cm2
                WHERE cm2.chat = :chat
            )
            """)
    Optional<ChatMessageEntity> getMessageWithMaxTimeStamp(@Param("chat") ChatEntity chat);

    @Query("""
            SELECT cm.messageNumber
            FROM ChatMessageEntity cm
            WHERE
            cm.timeStamp = (
                SELECT MAX(cm2.timeStamp)
                FROM ChatMessageEntity cm2
                WHERE cm2.chat = :chat
            )
            """)
    Optional<Long> findLastMessageNumber(ChatEntity chat);

    @Query("""
            SELECT cm.messageNumber
            FROM ChatMessageEntity cm
            WHERE
            cm.timeStamp = (
                SELECT MAX(cm2.timeStamp)
                FROM ChatMessageEntity cm2
                WHERE cm2.chat.tag  = :chatTag
            )
            """)
    Optional<Long> findLastMessageNumber(String chatTag);
}
