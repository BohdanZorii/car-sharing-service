package com.zorii.carsharing.service.impl;

import com.zorii.carsharing.service.NotificationService;
import com.zorii.carsharing.telegram.TelegramAbilityBot;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
  private final TelegramAbilityBot telegramBot;

  @Async
  @Override
  public void sendNotification(String message, Long telegramChatId) {
    telegramBot.sendMessageToUser(message, telegramChatId);
  }
}
