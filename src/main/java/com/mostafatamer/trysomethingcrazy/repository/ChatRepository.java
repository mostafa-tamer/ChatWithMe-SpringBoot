package com.mostafatamer.trysomethingcrazy.repository;

import com.mostafatamer.trysomethingcrazy.domain.dto.chat.ChatDto;
import com.mostafatamer.trysomethingcrazy.domain.entity.ChatEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.ChatMessageEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

    @Deprecated
    @Query("SELECT c FROM ChatEntity c WHERE SIZE(c.members) = 2 AND :user1 MEMBER OF c.members AND :user2 MEMBER OF c.members")
    Optional<ChatEntity> findChatByUsers(@Param("user1") UserEntity user1, @Param("user2") UserEntity user2);

    @Query("SELECT c FROM ChatEntity c WHERE :user MEMBER OF c.members")
    List<ChatEntity> findByUser(UserEntity user);

    @Query("SELECT c FROM ChatEntity c WHERE :user MEMBER OF c.members")
    Page<ChatEntity> findByUser(UserEntity user, Pageable pageable);


    Optional<ChatEntity> findByTag(String chatTag);

    @Query("""
            SELECT m
            FROM ChatMessageEntity m
            WHERE m.messageNumber = (
                SELECT MAX(m2.messageNumber)
                FROM ChatMessageEntity m2
                WHERE m2.chat = :chatEntity
            ) AND m.chat = :chatEntity""")
    Optional<ChatMessageEntity> findLastMessage(@Param("chatEntity") ChatEntity chatEntity);
}
