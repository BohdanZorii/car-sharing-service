package com.zorii.carsharing.exception;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class NotificationException extends RuntimeException {

  public NotificationException(String message, TelegramApiException ex) {
  }
}
