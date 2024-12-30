package com.zorii.carsharing.service.impl;

import com.zorii.carsharing.dto.UserLoginRequestDto;
import com.zorii.carsharing.dto.user.UserLoginResponseDto;
import com.zorii.carsharing.model.User;
import com.zorii.carsharing.repository.UserRepository;
import com.zorii.carsharing.security.JwtUtil;
import com.zorii.carsharing.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;

  @Override
  public UserLoginResponseDto authenticate(UserLoginRequestDto request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password()));
    String token = jwtUtil.generateToken(authentication.getName());
    return new UserLoginResponseDto(token);
  }

  @Override
  public void authenticateWithTelegram(UserLoginRequestDto requestDto, Long telegramId) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(requestDto.email(),
            requestDto.password())
    );
    User user = (User) authentication.getPrincipal();
    user.setTelegramChatId(telegramId);
    userRepository.save(user);
  }
}
