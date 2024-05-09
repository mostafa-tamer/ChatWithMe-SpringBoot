package com.mostafatamer.trysomethingcrazy.controller;

import com.mostafatamer.trysomethingcrazy.domain.dto.UserDto;
import com.mostafatamer.trysomethingcrazy.mappers.impl.UserMapper;
import com.mostafatamer.trysomethingcrazy.service.FriendshipService;
import com.mostafatamer.trysomethingcrazy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;



    @GetMapping("/users")
    List<UserDto> users() {
        var users = userService.findAll();
        log.info(users.toString());
        return users.stream().map(userMapper::entityToDto).toList();
    }

}
