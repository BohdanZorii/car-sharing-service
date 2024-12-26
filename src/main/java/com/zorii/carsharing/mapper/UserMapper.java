package com.zorii.carsharing.mapper;

import com.zorii.carsharing.dto.user.UserRegistrationDto;
import com.zorii.carsharing.dto.user.UserResponseDto;
import com.zorii.carsharing.dto.user.UserUpdateDto;
import com.zorii.carsharing.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

  User toEntity(UserRegistrationDto dto);

  UserResponseDto toResponseDto(User user);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "email", ignore = true)
  @Mapping(target = "password", ignore = true)
  void updateUserFromDto(UserUpdateDto dto, @MappingTarget User user);
}
