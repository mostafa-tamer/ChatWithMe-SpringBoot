package com.mostafatamer.trysomethingcrazy.repository;

import com.mostafatamer.trysomethingcrazy.domain.entity.FriendRequestCompositeKey;
import com.mostafatamer.trysomethingcrazy.domain.entity.FriendRequestEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendRequestEntity, FriendRequestCompositeKey> {

    List<FriendRequestEntity> findByFriendRequestCompositeKeyReceiver(UserEntity userEntity);

    Optional<FriendRequestEntity> findByFriendRequestCompositeKey(FriendRequestCompositeKey friendRequestCompositeKey);

    Optional<FriendRequestEntity> findByFriendRequestCompositeKeySenderIdAndFriendRequestCompositeKeyReceiverId(Long sender, Long receiver);

    @Transactional
    void deleteByFriendRequestCompositeKey(FriendRequestCompositeKey friendRequestCompositeKey);

}
