package com.mostafatamer.trysomethingcrazy.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mostafatamer.trysomethingcrazy.domain.entity.UserEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.RoleEntity;
import com.mostafatamer.trysomethingcrazy.domain.entity.Roles;
import com.mostafatamer.trysomethingcrazy.repository.RoleRepository;
import com.mostafatamer.trysomethingcrazy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;

@Configuration
@RequiredArgsConstructor
public class Config {

    private final RoleRepository roleRepository;

    @Bean
    ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE)    // for handling nested objects
                .setAmbiguityIgnored(true)
                .setFieldMatchingEnabled(true);
        return modelMapper;
    }

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    CommandLineRunner runner() {
        return args -> {
            roles();
        };
    }

    private void roles() {
        if (roleRepository.findByName(Roles.USER.name()).isEmpty())
            roleRepository.save(RoleEntity.builder().name(Roles.USER.name()).build());
    }
}