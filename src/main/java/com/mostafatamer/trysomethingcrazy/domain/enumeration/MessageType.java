package com.mostafatamer.trysomethingcrazy.domain.enumeration;

import lombok.Getter;

@Getter
public enum MessageType {
    FRIEND_CHAT_MESSAGE("FRIEND_CHAT_MESSAGE"),
    FRIEND_REQUEST_ACCEPTED("FRIEND_REQUEST_ACCEPTED"),
    ADD_TO_GROUP("ADD_TO_GROUP"),
    FRIEND_REQUEST("FRIEND_REQUEST");


    private final String value;

    MessageType(String value) {
        this.value = value;
    }

}
