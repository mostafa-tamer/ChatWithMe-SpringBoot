package com.mostafatamer.trysomethingcrazy.domain.auth;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
@Builder
public class RegistrationResponse {
    String username;
    String nickname;
}
