package com.zorii.carsharing.controller;

import com.zorii.carsharing.dto.user.RoleDto;
import com.zorii.carsharing.dto.user.UserResponseDto;
import com.zorii.carsharing.dto.user.UserUpdateDto;
import com.zorii.carsharing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "basicAuth") // Додає Basic Auth до всіх ендпоінтів у цьому контролері
public class UserController {

  private final UserService userService;

  @Operation(
      summary = "Get profile info of the authenticated user",
      responses = {
          @ApiResponse(responseCode = "200", description = "Profile info retrieved"),
          @ApiResponse(responseCode = "401", description = "Unauthorized")
      }
  )
  @GetMapping("/me")
  public ResponseEntity<UserResponseDto> getUserProfile() {
    String email = getAuthenticatedUserEmail();
    UserResponseDto userProfile = userService.getUserProfile(email);
    return ResponseEntity.ok(userProfile);
  }

  @Operation(
      summary = "Update profile info of the authenticated user",
      responses = {
          @ApiResponse(responseCode = "200", description = "Profile info updated"),
          @ApiResponse(responseCode = "400", description = "Invalid request data"),
          @ApiResponse(responseCode = "401", description = "Unauthorized")
      }
  )
  @PutMapping("/me")
  public ResponseEntity<UserResponseDto> updateUserProfile(@Valid @RequestBody UserUpdateDto dto) {
    String email = getAuthenticatedUserEmail();
    UserResponseDto updatedProfile = userService.updateUserProfile(email, dto);
    return ResponseEntity.ok(updatedProfile);
  }

  @Operation(
      summary = "Update the role of a user by ID",
      responses = {
          @ApiResponse(responseCode = "200", description = "User role updated"),
          @ApiResponse(responseCode = "400", description = "Invalid request data"),
          @ApiResponse(responseCode = "404", description = "User not found"),
          @ApiResponse(responseCode = "401", description = "Unauthorized")
      }
  )
  @PutMapping("/{id}/role")
  public ResponseEntity<UserResponseDto> updateUserRole(
      @PathVariable UUID id,
      @Valid @RequestBody RoleDto roleDto
  ) {
    UserResponseDto updatedUser = userService.updateUserRole(id, roleDto);
    return ResponseEntity.ok(updatedUser);
  }

  private String getAuthenticatedUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new SecurityException("No authenticated user found");
    }
    return authentication.getName();
  }
}
