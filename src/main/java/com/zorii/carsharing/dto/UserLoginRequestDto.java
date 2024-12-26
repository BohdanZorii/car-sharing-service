package com.zorii.carsharing.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
    @Email(message = "Invalid email format")
    @NotBlank
    @Size(max = 255, message = "Email must be less than 256 characters")
    String email,
    @NotBlank
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters long")
    String password) {
}
