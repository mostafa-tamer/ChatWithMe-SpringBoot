package com.mostafatamer.trysomethingcrazy.domain.firebase;


import com.mostafatamer.trysomethingcrazy.domain.dto.chat.ChatDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Setter
@Getter
public class ChatMessage implements Serializable {
    private String title;
    private String message;
    private ChatDto chatDto;
}
