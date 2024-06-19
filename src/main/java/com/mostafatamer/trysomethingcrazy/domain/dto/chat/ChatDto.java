package com.mostafatamer.trysomethingcrazy.domain.dto.chat;

import com.mostafatamer.trysomethingcrazy.domain.dto.UserDto;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ChatDto {
    String tag;
    List<UserDto> members;
    ChatMessageDto lastMessage;
}