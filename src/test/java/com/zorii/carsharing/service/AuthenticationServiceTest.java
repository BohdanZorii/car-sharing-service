package com.zorii.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zorii.carsharing.dto.UserLoginRequestDto;
import com.zorii.carsharing.dto.user.UserLoginResponseDto;
import com.zorii.carsharing.model.User;
import com.zorii.carsharing.repository.UserRepository;
import com.zorii.carsharing.security.JwtUtil;
import com.zorii.carsharing.service.impl.AuthenticationServiceImpl;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private User user;
    private UserLoginRequestDto loginRequestDto;
    private UserLoginResponseDto loginResponseDto;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("securePassword");
        user.setRole(User.Role.CUSTOMER);

        loginRequestDto = new UserLoginRequestDto("test@example.com", "securePassword");

        loginResponseDto = new UserLoginResponseDto("jwtToken");

        authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn(user.getEmail());
        lenient().when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    void authenticate_ValidCredentials_ReturnsToken() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(user.getEmail())).thenReturn("jwtToken");

        UserLoginResponseDto actual = authenticationService.authenticate(loginRequestDto);

        assertEquals(loginResponseDto.token(), actual.token());
    }

    @Test
    void authenticateWithTelegram_ValidRequest_UpdatesUserTelegramId() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authenticationService.authenticateWithTelegram(loginRequestDto, 123456789L);

        assertEquals(123456789L, user.getTelegramChatId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void authenticate_InvalidCredentials_ThrowsException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Bad credentials") {});

        Exception exception = assertThrows(AuthenticationException.class, 
            () -> authenticationService.authenticate(loginRequestDto));

        assertEquals("Bad credentials", exception.getMessage());
    }
}
