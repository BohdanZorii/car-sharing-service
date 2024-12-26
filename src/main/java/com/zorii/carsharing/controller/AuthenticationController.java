package com.zorii.carsharing.controller;

import com.zorii.carsharing.dto.UserLoginRequestDto;
import com.zorii.carsharing.dto.user.UserLoginResponseDto;
import com.zorii.carsharing.dto.user.UserRegistrationDto;
import com.zorii.carsharing.dto.user.UserResponseDto;
import com.zorii.carsharing.service.AuthenticationService;
import com.zorii.carsharing.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final UserService userService;
  private final AuthenticationService authenticationService;

  @Operation(summary = "Register a new user",
      responses = {
          @ApiResponse(responseCode = "201", description = "User registered"),
          @ApiResponse(responseCode = "400", description = "Invalid request data")
      })
  @PostMapping("/registration")
  public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRegistrationDto dto) {
    UserResponseDto registeredUser = userService.registerUser(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
  }

  @PostMapping("/login")
  @Operation(summary = "User login", description = "Authenticates a user and returns a token.")
  public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
    return authenticationService.authenticate(request);
  }
}
