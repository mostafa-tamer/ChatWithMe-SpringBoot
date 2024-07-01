package com.mostafatamer.trysomethingcrazy.domain.firebase;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mostafatamer.trysomethingcrazy.domain.enumeration.MessageType;
import lombok.*;

import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CloudMessage<T> {
    MessageType messageType;
    T data;

    @SneakyThrows
    @Override
    public String toString() {
        var objectMapper = new ObjectMapper();
        objectMapper.setVisibility(FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper.writeValueAsString(this);
    }
}