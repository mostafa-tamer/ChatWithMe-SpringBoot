package com.mostafatamer.trysomethingcrazy.domain.auth;

import com.mostafatamer.trysomethingcrazy.domain.dto.UserDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationResponse {
    String token;
    UserDto user;
}
