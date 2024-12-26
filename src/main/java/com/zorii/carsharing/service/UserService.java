package com.zorii.carsharing.service;

import com.zorii.carsharing.dto.user.RoleDto;
import com.zorii.carsharing.dto.user.UserRegistrationDto;
import com.zorii.carsharing.dto.user.UserResponseDto;
import com.zorii.carsharing.dto.user.UserUpdateDto;
import java.util.UUID;

public interface UserService {

  UserResponseDto registerUser(UserRegistrationDto dto);

  UserResponseDto getUserProfile(String email);

  UserResponseDto updateUserProfile(String email, UserUpdateDto dto);

  UserResponseDto updateUserRole(UUID id, RoleDto role);
}
