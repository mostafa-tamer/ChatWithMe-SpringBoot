package com.mostafatamer.trysomethingcrazy.domain.dto.firendRequest;

import com.mostafatamer.trysomethingcrazy.domain.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestDto {
    UserDto sender;
    String message;
}
