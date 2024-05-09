package com.mostafatamer.trysomethingcrazy.domain.firebase;


import com.mostafatamer.trysomethingcrazy.domain.dto.UserDto;
import com.mostafatamer.trysomethingcrazy.domain.dto.chat.ChatDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Setter
@Getter
public class FriendRequest {
    private String message;
    private UserDto sender;
}
