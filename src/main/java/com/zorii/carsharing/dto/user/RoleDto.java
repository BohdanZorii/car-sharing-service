package com.zorii.carsharing.dto.user;

import com.zorii.carsharing.validation.EnumValue;

public record RoleDto(
    @EnumValue(enumValues = {"MANAGER", "CUSTOMER"}, message = "Invalid role")
    String role
) {}
