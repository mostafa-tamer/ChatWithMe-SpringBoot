package com.mostafatamer.trysomethingcrazy.repository;

import com.mostafatamer.trysomethingcrazy.domain.entity.ChatEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends CrudRepository<ChatEntity, Long> {

    @Query("SELECT c FROM ChatEntity c WHERE SIZE(c.users) = 2 AND :user1 MEMBER OF c.users AND :user2 MEMBER OF c.users")
    Optional<ChatEntity> findChatByUsers(@Param("user1") UserEntity user1, @Param("user2") UserEntity user2);


    Optional<ChatEntity> findByTag(String chatTag);

}
