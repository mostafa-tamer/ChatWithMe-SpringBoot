package com.mostafatamer.trysomethingcrazy.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Table(name = "friend_requests")
public class FriendRequestEntity {
    @EmbeddedId
    FriendRequestCompositeKey friendRequestCompositeKey;

    String message;

}




