package com.mostafatamer.trysomethingcrazy.domain.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestCompositeKey implements Serializable {

    Long senderId;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    UserEntity receiver;
}
