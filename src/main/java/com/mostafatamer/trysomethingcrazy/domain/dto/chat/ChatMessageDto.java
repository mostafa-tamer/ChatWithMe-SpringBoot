package com.mostafatamer.trysomethingcrazy.domain.dto.chat;

import com.mostafatamer.trysomethingcrazy.domain.dto.UserDto;
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
    UserDto sender;
    Long timeStamp;
    Long messageNumber;
}