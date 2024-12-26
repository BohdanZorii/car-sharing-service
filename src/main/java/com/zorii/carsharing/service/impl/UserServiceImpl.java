package com.zorii.carsharing.service.impl;

import com.zorii.carsharing.dto.user.RoleDto;
import com.zorii.carsharing.dto.user.UserRegistrationDto;
import com.zorii.carsharing.dto.user.UserResponseDto;
import com.zorii.carsharing.dto.user.UserUpdateDto;
import com.zorii.carsharing.mapper.UserMapper;
import com.zorii.carsharing.model.User;
import com.zorii.carsharing.model.User.Role;
import com.zorii.carsharing.repository.UserRepository;
import com.zorii.carsharing.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserResponseDto registerUser(UserRegistrationDto dto) {
    User user = userMapper.toEntity(dto);
    user.setPassword(passwordEncoder.encode(dto.password()));
    User savedUser = userRepository.save(user);
    return userMapper.toResponseDto(savedUser);
  }

  @Transactional(readOnly = true)
  @Override
  public UserResponseDto getUserProfile(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    return userMapper.toResponseDto(user);
  }

  @Transactional
  @Override
  public UserResponseDto updateUserProfile(String email, UserUpdateDto dto) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    userMapper.updateUserFromDto(dto, user);
    return userMapper.toResponseDto(user);
  }

  @Transactional
  @Override
  public UserResponseDto updateUserRole(UUID id, RoleDto dto) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    Role role = Role.valueOf(dto.role());
    user.setRole(role);
    return userMapper.toResponseDto(user);
  }
}


