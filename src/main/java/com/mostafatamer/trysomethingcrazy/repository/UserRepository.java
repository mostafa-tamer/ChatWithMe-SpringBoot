package com.mostafatamer.trysomethingcrazy.repository;

import com.mostafatamer.trysomethingcrazy.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsernameAndPassword(String username, String password);
    Optional<UserEntity> findByUsername(String username);

    List<UserEntity> findByFirebaseToken(String firebaseToken);
}
