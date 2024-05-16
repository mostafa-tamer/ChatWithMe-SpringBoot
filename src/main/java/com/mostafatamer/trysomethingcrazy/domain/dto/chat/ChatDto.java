package com.mostafatamer.trysomethingcrazy.domain.dto.chat;

import com.mostafatamer.trysomethingcrazy.domain.dto.UserDto;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ChatDto {
    String tag;
    UserDto friend;
}