package com.mostafatamer.trysomethingcrazy.domain.dto.chat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatLastMessage {
    String tag;
    Long lastMessageNumber;
}
