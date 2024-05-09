package com.mostafatamer.trysomethingcrazy.domain.dto.firendRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendFriendRequestDto {
    String receiverUsername;
    String message;
}