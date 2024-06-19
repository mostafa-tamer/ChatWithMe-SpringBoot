package com.mostafatamer.trysomethingcrazy.domain.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Setter
@Getter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    @NotBlank(message = "Username is mandatory")
    String username;
    @Size(min = 8, message = "Password should be 8 characters or more")
    String password;
    @NotBlank(message = "Nickname is mandatory")
    String nickname;

    String firebaseToken;
}
