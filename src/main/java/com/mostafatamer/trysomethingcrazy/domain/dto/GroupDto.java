package com.mostafatamer.trysomethingcrazy.domain.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDto {
    String IdentificationKey;   //should be placed automatically when insert
    List<UserDto> users;
}
