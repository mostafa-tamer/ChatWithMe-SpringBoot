package com.mostafatamer.trysomethingcrazy.service;

import com.mostafatamer.trysomethingcrazy.domain.auth.AuthenticationRequest;
import com.mostafatamer.trysomethingcrazy.domain.auth.AuthenticationResponse;
import com.mostafatamer.trysomethingcrazy.domain.auth.RegistrationRequest;
import com.mostafatamer.trysomethingcrazy.domain.dto.UserDto;
import com.mostafatamer.trysomethingcrazy.domain.entity.UserEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.RoleEntity;
import com.mostafatamer.trysomethingcrazy.repository.RoleRepository;
import com.mostafatamer.trysomethingcrazy.repository.UserRepository;
import com.mostafatamer.trysomethingcrazy.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserDto register(RegistrationRequest registrationRequest) {
        RoleEntity userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("role not found"));

        UserEntity user = UserEntity.builder()
                .nickname(registrationRequest.getNickname())
                .username(registrationRequest.getUsername())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .firebaseToken(registrationRequest.getFirebaseToken())
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);

        return UserDto.builder()
                .nickname(registrationRequest.getNickname())
                .username(registrationRequest.getUsername())
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        UserEntity user = (UserEntity) authentication.getPrincipal();
        user.setFirebaseToken(request.getFirebaseToken());

        List<UserEntity> usersWithRequestToken = userRepository.findByFirebaseToken(request.getFirebaseToken());

        List<UserEntity> updatedUsers = usersWithRequestToken.stream().peek(userEntity -> {
            if (!userEntity.getUsername().equals(user.getUsername())) {
                userEntity.setFirebaseToken(null);
            }
        }).toList();

        userRepository.save(user);
        userRepository.saveAll(updatedUsers);

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(UserDto.builder()
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .build())
                .build();
    }

    public static UserEntity getUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserEntity) authentication.getPrincipal();
    }
}
