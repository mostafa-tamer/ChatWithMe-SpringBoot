package com.mostafatamer.trysomethingcrazy.service;

import com.mostafatamer.trysomethingcrazy.domain.entity.FriendRequestCompositeKey;
import com.mostafatamer.trysomethingcrazy.domain.entity.FriendRequestEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.UserEntity;
import com.mostafatamer.trysomethingcrazy.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;

    public FriendRequestEntity save(FriendRequestEntity friendRequest) {
        return friendshipRepository.save(friendRequest);
    }


    public List<FriendRequestEntity> findAllByReceiver(UserEntity user) {
        return friendshipRepository.findByFriendRequestCompositeKeyReceiver(user);
    }

//    public Optional<FriendRequestEntity> findByFriendRequestCompositeKey(FriendRequestCompositeKey friendRequestCompositeKey) {
//        return friendshipRepository.findByFriendRequestCompositeKey(friendRequestCompositeKey);
//    }


    public boolean isFriendRequestAlreadySent(Long senderId, Long receiverId) {
        return friendshipRepository.findByFriendRequestCompositeKeySenderIdAndFriendRequestCompositeKeyReceiverId(
                senderId,
                receiverId
        ).isPresent();
    }

    public void deleteFriendRequestsById(FriendRequestCompositeKey friendRequestCompositeKey) {
        friendshipRepository.deleteByFriendRequestCompositeKey(friendRequestCompositeKey);
    }
}
