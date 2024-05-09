package com.mostafatamer.trysomethingcrazy.domain.dto;

import lombok.*;

@Data
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    String nickname;
    String username;
}
