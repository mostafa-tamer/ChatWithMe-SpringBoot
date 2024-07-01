package com.mostafatamer.trysomethingcrazy;

import com.mostafatamer.trysomethingcrazy.constants.MessageBrokers;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(
                MessageBrokers.SEND_MESSAGE_TO_CHAT,
                MessageBrokers.SEND_FRIEND_REQUEST,
                MessageBrokers.ACCEPT_FRIEND_REQUEST,
                MessageBrokers.ADD_TO_GROUP,
                MessageBrokers.REMOVE_FRIEND
        );

        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket");
    }

}