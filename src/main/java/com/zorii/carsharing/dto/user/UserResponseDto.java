package com.zorii.carsharing.dto.user;

import java.util.UUID;

public record UserResponseDto(
    UUID id,
    String email,
    String firstName,
    String lastName,
    String role
) {}
