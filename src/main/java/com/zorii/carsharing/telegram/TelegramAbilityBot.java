package com.zorii.carsharing.telegram;

import com.zorii.carsharing.config.BotConfig;
import com.zorii.carsharing.dto.UserLoginRequestDto;
import com.zorii.carsharing.exception.NotificationException;
import com.zorii.carsharing.service.AuthenticationService;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class TelegramAbilityBot extends AbilityBot {

  private final BotConfig botConfig;
  private final AuthenticationService authenticationService;
  private final ConcurrentMap<Long, String> userEmails = new ConcurrentHashMap<>();
  private final ConcurrentMap<Long, BotState> userStates = new ConcurrentHashMap<>();

  public TelegramAbilityBot(BotConfig botConfig, AuthenticationService authenticationService) {
    super(botConfig.getBotName(), botConfig.getBotToken());
    this.botConfig = botConfig;
    this.authenticationService = authenticationService;

  }

  @PostConstruct
  private void init() {
    try {
      TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
      botsApi.registerBot(this);
    } catch (TelegramApiException e) {
      throw new NotificationException("Can`t start a telegram bot ", e);
    }
  }

  @Override
  public long creatorId() {
    return 1L;
  }

  @Override
  public String getBotToken() {
    return botConfig.getBotToken();
  }

  public Ability start() {
    return Ability.builder()
        .name("start")
        .info("Welcome message")
        .locality(Locality.ALL)
        .privacy(Privacy.PUBLIC)
        .action(ctx -> {
          String message = "Welcome! This bot will send you updates about bookings.\nType '/login' to continue.";
          silent.send(message, ctx.chatId());
        })
        .build();
  }

  public Ability login() {
    return Ability.builder()
        .name("login")
        .info("Login ability")
        .locality(Locality.ALL)
        .privacy(Privacy.PUBLIC)
        .action(ctx -> {
          silent.send("Enter your email", ctx.chatId());
          userStates.put(ctx.chatId(), BotState.LOGIN_EMAIL);
        })
        .build();
  }

  public Ability cancel() {
    return Ability.builder()
        .name("cancel")
        .info("Cancel current operation")
        .locality(Locality.ALL)
        .privacy(Privacy.PUBLIC)
        .action(ctx -> {
          userStates.put(ctx.chatId(), BotState.IDLE);
          userEmails.remove(ctx.chatId());
          silent.send("Current operation was canceled", ctx.chatId());
        })
        .build();
  }

  public void sendMessageToUser(String text, Long id) {
    try {
      SendMessage sendMessage = new SendMessage(id.toString(), text);
      sendMessage.enableMarkdown(true);
      execute(sendMessage);
    } catch (TelegramApiException e) {
      throw new NotificationException("Can`t send message to user with id: " + id, e);
    }
  }

  @Override
  public void onUpdateReceived(Update update) {
    String message = update.getMessage().getText();
    Long chatId = update.getMessage().getChatId();

    if (message != null && !message.startsWith("/")) {
      BotState currentState = userStates.getOrDefault(chatId, BotState.IDLE);
      switch (currentState) {
        case LOGIN_EMAIL -> handleEmailInput(update);
        case LOGIN_PASSWORD -> handlePasswordInput(update);
      }
    }

    super.onUpdateReceived(update);
  }

  private void handleEmailInput(Update update) {
    Long chatId = update.getMessage().getChatId();
    String text = update.getMessage().getText();

    userEmails.put(chatId, text);
    userStates.put(chatId, BotState.LOGIN_PASSWORD);

    silent.send("Enter your password", chatId);
  }

  private void handlePasswordInput(Update update) {
    Long chatId = update.getMessage().getChatId();
    String text = update.getMessage().getText();

    String email = userEmails.get(chatId);

    UserLoginRequestDto requestDto = new UserLoginRequestDto(email, text);

    try {
      authenticationService.authenticateWithTelegram(requestDto, chatId);

      silent.send("Success!", chatId);
      silent.send("From now on you will receive notification on every booking.", chatId);

      userEmails.remove(chatId);
      userStates.put(chatId, BotState.IDLE);
    } catch (AuthenticationException e) {
      silent.send("Wrong email or password!", chatId);
      silent.send("Try again or press /cancel to exit.", chatId);
    }
  }

}
