package com.mostafatamer.trysomethingcrazy.domain;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class ApiResponse<T> implements Serializable {
    T data;
    ApiError apiError;
}
