package com.mostafatamer.trysomethingcrazy.controller;


import com.mostafatamer.trysomethingcrazy.domain.ApiResponse;
import com.mostafatamer.trysomethingcrazy.domain.auth.AuthenticationRequest;
import com.mostafatamer.trysomethingcrazy.domain.auth.AuthenticationResponse;
import com.mostafatamer.trysomethingcrazy.domain.auth.RegistrationRequest;
import com.mostafatamer.trysomethingcrazy.domain.dto.UserDto;
import com.mostafatamer.trysomethingcrazy.mappers.impl.UserMapper;
import com.mostafatamer.trysomethingcrazy.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.*;


@Log
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final  AuthenticationService authenticationService ;


    @PostMapping("/register")
    ApiResponse<UserDto> register(
            @RequestBody @Valid RegistrationRequest registrationRequest
    ) {

        return ApiResponse.<UserDto>builder()
                .data(authenticationService.register(registrationRequest))
                .build();
    }

    @PostMapping("/authenticate")
    ApiResponse<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        return ApiResponse.<AuthenticationResponse>builder()
                .data(authenticationService.authenticate(request))
                .build();
    }

    @GetMapping("/hello")
    String greetings() {
        return "Greetings from Chat with Me!";
    }
}
