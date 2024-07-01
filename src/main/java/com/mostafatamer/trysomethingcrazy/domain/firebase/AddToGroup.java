package com.mostafatamer.trysomethingcrazy.domain.firebase;

import com.mostafatamer.trysomethingcrazy.domain.dto.chat.ChatDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class AddToGroup {
    ChatDto groupChat;
}
