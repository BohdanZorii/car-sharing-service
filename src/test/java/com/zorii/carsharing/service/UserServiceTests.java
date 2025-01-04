package com.zorii.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zorii.carsharing.dto.user.RoleDto;
import com.zorii.carsharing.dto.user.UserRegistrationDto;
import com.zorii.carsharing.dto.user.UserResponseDto;
import com.zorii.carsharing.dto.user.UserUpdateDto;
import com.zorii.carsharing.exception.DuplicateEmailException;
import com.zorii.carsharing.mapper.UserMapper;
import com.zorii.carsharing.model.User;
import com.zorii.carsharing.repository.UserRepository;
import com.zorii.carsharing.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserServiceImpl userService;

  private User user;
  private UserRegistrationDto registrationDto;
  private UserResponseDto responseDto;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("test@example.com");
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setPassword("encodedPassword");
    user.setRole(User.Role.CUSTOMER);

    registrationDto = new UserRegistrationDto(
        "test@example.com",
        "John",
        "Doe",
        "password123"
    );

    responseDto = new UserResponseDto(
        user.getId(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getRole().name()
    );
  }

  @Test
  @DisplayName("Register user with unique email")
  void registerUser_UniqueEmail_ReturnsUserResponseDto() {
    when(userRepository.existsByEmail(registrationDto.email())).thenReturn(false);
    when(userMapper.toEntity(registrationDto)).thenReturn(user);
    when(passwordEncoder.encode(registrationDto.password())).thenReturn("encodedPassword");
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toResponseDto(user)).thenReturn(responseDto);

    UserResponseDto actual = userService.registerUser(registrationDto);

    assertEquals(responseDto, actual);
    verify(userRepository, times(1)).save(user);
  }

  @Test
  @DisplayName("Register user with duplicate email throws exception")
  void registerUser_DuplicateEmail_ThrowsException() {
    when(userRepository.existsByEmail(registrationDto.email())).thenReturn(true);

    assertThrows(DuplicateEmailException.class, () -> userService.registerUser(registrationDto));

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("Get user profile by email")
  void getUserProfile_EmailExists_ReturnsUserResponseDto() {
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    when(userMapper.toResponseDto(user)).thenReturn(responseDto);

    UserResponseDto actual = userService.getUserProfile(user.getEmail());

    assertEquals(responseDto, actual);
  }

  @Test
  @DisplayName("Get user profile by non-existent email throws exception")
  void getUserProfile_EmailNotFound_ThrowsException() {
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> userService.getUserProfile(user.getEmail()));
  }

  @Test
  @DisplayName("Update user profile by email")
  void updateUserProfile_EmailExists_ReturnsUpdatedUserResponseDto() {
    UserUpdateDto updateDto = new UserUpdateDto("UpdatedFirstName", "UpdatedLastName");

    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    doNothing().when(userMapper).updateUserFromDto(updateDto, user);
    when(userMapper.toResponseDto(user)).thenReturn(responseDto);

    UserResponseDto actual = userService.updateUserProfile(user.getEmail(), updateDto);

    assertEquals(responseDto, actual);
    verify(userMapper, times(1)).updateUserFromDto(updateDto, user);
  }

  @Test
  @DisplayName("Update user role by ID")
  void updateUserRole_IdExists_ReturnsUpdatedUserResponseDto() {
    RoleDto roleDto = new RoleDto("MANAGER");

    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(userMapper.toResponseDto(user)).thenReturn(responseDto);

    UserResponseDto actual = userService.updateUserRole(user.getId(), roleDto);

    assertEquals(responseDto, actual);
    assertEquals(User.Role.MANAGER, user.getRole());
  }

  @Test
  @DisplayName("Update user role with non-existent ID throws exception")
  void updateUserRole_IdNotFound_ThrowsException() {
    RoleDto roleDto = new RoleDto("MANAGER");

    when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> userService.updateUserRole(user.getId(), roleDto));
  }
}
