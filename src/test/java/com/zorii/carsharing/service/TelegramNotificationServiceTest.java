package com.zorii.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.zorii.carsharing.exception.NotificationException;
import com.zorii.carsharing.service.impl.TelegramNotificationService;
import com.zorii.carsharing.telegram.TelegramAbilityBot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TelegramNotificationServiceTest {

    @Mock
    private TelegramAbilityBot telegramBot;

    @InjectMocks
    private TelegramNotificationService telegramNotificationService;

    @Test
    void sendNotification_ValidMessageAndChatId_CallsSendMessageToUser() {
        String message = "Test notification message";
        Long chatId = 123456789L;

        telegramNotificationService.sendNotification(message, chatId);

        verify(telegramBot, times(1)).sendMessageToUser(message, chatId);
    }
}
