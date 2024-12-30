package com.zorii.carsharing.service;

public interface NotificationService {
  void sendNotification(String message, Long recipientId);
}
