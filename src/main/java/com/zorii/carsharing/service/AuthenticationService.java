package com.zorii.carsharing.service;

import com.zorii.carsharing.dto.UserLoginRequestDto;
import com.zorii.carsharing.dto.user.UserLoginResponseDto;

public interface AuthenticationService {
  UserLoginResponseDto authenticate(UserLoginRequestDto request);

  void authenticateWithTelegram(UserLoginRequestDto requestDto, Long telegramId);
}
