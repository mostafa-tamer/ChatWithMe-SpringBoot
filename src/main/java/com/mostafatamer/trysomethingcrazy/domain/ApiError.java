package com.mostafatamer.trysomethingcrazy.domain;


import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiError {
    String message;
    String clientMessage;
    List<String> validationMessages;
}