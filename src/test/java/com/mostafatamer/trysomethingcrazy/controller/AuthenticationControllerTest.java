package com.mostafatamer.trysomethingcrazy.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mostafatamer.trysomethingcrazy.domain.auth.RegistrationRequest;
import com.mostafatamer.trysomethingcrazy.domain.entity.UserEntity;
import com.mostafatamer.trysomethingcrazy.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class AuthenticationControllerTest {
    ObjectMapper objectMapper = new ObjectMapper();

    MockMvc mockMvc;

    UserService userService;

    @Autowired
    public AuthenticationControllerTest(MockMvc mockMvc, UserService userService) {
        this.mockMvc = mockMvc;
        this.userService = userService;
    }

    @Test
    void testUserRegisterAndAssertThatUserSavedInDatabase() throws Exception {
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .nickname("Mostafa Tamer")
                .username("mostafasda32443")
                .password("12345678")
                .firebaseToken("dummyToken")
                .build();

        String content = objectMapper.writeValueAsString(registrationRequest);


        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.data.nickname").value("Mostafa Tamer")
                ).andExpect(
                        MockMvcResultMatchers.jsonPath("$.data.username").value("mostafasda32443")
                ).andDo(print());

        UserEntity userEntity = userService.findByUsername("mostafasda32443");

        assertThat(userEntity.getUsername()).isEqualTo(registrationRequest.getUsername());
    }
}
