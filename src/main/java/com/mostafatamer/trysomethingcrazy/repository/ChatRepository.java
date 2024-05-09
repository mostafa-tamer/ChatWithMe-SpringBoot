package com.mostafatamer.trysomethingcrazy.repository;

import com.mostafatamer.trysomethingcrazy.domain.entity.ChatEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends CrudRepository<ChatEntity, Long> {

    @Query("SELECT c FROM ChatEntity c JOIN c.users u WHERE u.id IN :userIds")
    Optional<ChatEntity> findChatByChatMembers(@Param("userIds") List<Long> userIds);

    Optional<ChatEntity> findByTag(String chatTag);

}
