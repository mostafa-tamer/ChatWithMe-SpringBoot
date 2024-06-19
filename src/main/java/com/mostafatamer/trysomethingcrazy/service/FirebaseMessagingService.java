package com.mostafatamer.trysomethingcrazy.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.mostafatamer.trysomethingcrazy.domain.firebase.CloudMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;


@Builder
@Getter
@Setter


@Service
public class FirebaseMessagingService {

//    private final String cloudMessage;

    public FirebaseMessagingService() {

    }

    @SneakyThrows
    public <T> void sendClientMessage(String token, CloudMessage<T> payload) {
        if (token != null) {
            Message msg = Message.builder()
                    .setToken(token)
                    .putData("cloud_message", payload.toString())
                    .build();
            FirebaseMessaging.getInstance().send(msg);
        }
    }

    @SneakyThrows
    <T> String sendTopicMessage(String topic, CloudMessage<T> payload) {
        Message msg = Message.builder()
                .setTopic("/topic")
                .putData("cloud_message", payload.toString())
                .build();

        return FirebaseMessaging.getInstance().send(msg);
    }
}
