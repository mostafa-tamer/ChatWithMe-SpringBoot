package com.mostafatamer.trysomethingcrazy.domain.firebase;

import com.mostafatamer.trysomethingcrazy.domain.dto.UserDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SecondaryRow;

@Setter
@Getter
@Builder
public class AcceptFriendRequest {
    UserDto receiver;
}