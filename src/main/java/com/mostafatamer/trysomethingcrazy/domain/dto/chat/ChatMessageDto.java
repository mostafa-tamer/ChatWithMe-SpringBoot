package com.mostafatamer.trysomethingcrazy.domain.dto.chat;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ChatMessageDto {
    String chatTag;
    @NotEmpty
    String message;
    @NotEmpty
    String senderUsername;
    Long timeStamp;
    Long messageNumber;
}