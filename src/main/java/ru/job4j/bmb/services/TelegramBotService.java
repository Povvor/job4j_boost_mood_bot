package ru.job4j.bmb.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.job4j.bmb.conditions.OnProdCondition;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.exception.SentContentException;
import ru.job4j.bmb.logic.AchievementService;
import ru.job4j.bmb.logic.ReminderService;
import ru.job4j.bmb.repository.MoodContentRepository;
import ru.job4j.bmb.repository.UserRepository;

import java.util.List;

@Service
@Conditional(OnProdCondition.class)
public class TelegramBotService extends TelegramLongPollingBot implements SentContent {
    private final BotCommandHandler handler;
    private final String botName;
    private final UserRepository userRepository;
    private final TgUI tgUI;
    private final MoodContentRepository moodRepository;
    private final ReminderService reminderService;

    public TelegramBotService(@Value("${telegram.bot.name}") String botName,
                              @Value("${telegram.bot.token}") String botToken,
                              BotCommandHandler handler,
                              UserRepository userRepository,
                              TgUI tgUI,
                              MoodContentRepository moodRepository, ReminderService reminderService) {
        super(botToken);
        this.handler = handler;
        this.botName = botName;
        this.userRepository = userRepository;
        this.tgUI = tgUI;
        this.moodRepository = moodRepository;
        this.reminderService = reminderService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("<UNK> <UNK> <UNK> <UNK> <UNK> <UNK>.");
        if (update.hasCallbackQuery()) {
            Content content = handler.handleCallback(update.getCallbackQuery()).orElseThrow();
            sent(content);
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage();
            sent(handler.commands(message).orElseThrow());
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void sent(Content content) {
        if (content.getAudio() != null) {
            sentAudio(content);
        } else if (content.getMarkup() != null) {
            sentMurkup(content);
        } else if (content.getPhoto() != null) {
            sentPhoto(content);
        } else {
            sentText(content);
        }
    }

    @Scheduled(fixedRateString = "${recommendation.alert.period}")
    public void remindUsers() {
        List<Content> contents = reminderService.remindUsers();
        for (Content content : contents) {
            sent(content);
        }
    }

    @Scheduled(fixedRateString = "${advice.alert.period}")
    public void adviceUsers() {
        List<Content> contents = reminderService.adviceUsers();
        for (Content content : contents) {
            sent(content);
        }
    }

    private void sentAudio(Content content) {
        SendAudio audio = new SendAudio();
        audio.setChatId(content.getChatId());
        audio.setAudio(content.getAudio());
        if (content.getText() != null) {
            audio.setCaption(content.getText());
        }
        try {
            execute(audio);
        } catch (TelegramApiException e) {
            throw new SentContentException("Ошибка при отправке аудио контента", e);
        }
    }

    private void sentMurkup(Content content) {
        SendMessage message = new SendMessage();
        message.setChatId(content.getChatId());
        if (content.getText() != null) {
            message.setText(content.getText());
        }
        message.setReplyMarkup(content.getMarkup());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new SentContentException("Ошибка при отправке Разметки", e);
        }
    }

    private void sentText(Content content) {
        SendMessage message = new SendMessage();
        message.setChatId(content.getChatId());
        message.setText(content.getText());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new SentContentException("Ошибка при отправке Текста", e);
        }
    }

    private void sentPhoto(Content content) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(content.getChatId());
        photo.setPhoto(content.getPhoto());
        if (content.getText() != null) {
            photo.setCaption(content.getText());
        }
        try {
            execute(photo);
        } catch (TelegramApiException e) {
            throw new SentContentException("Ошибка при отправке изображения", e);
        }
    }

    public void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public SendMessage sendButtons(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Как настроение сегодня?");
        message.setReplyMarkup(tgUI.buildButtons());
        return message;
    }

    public void register() throws TelegramApiException {
        var botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
        System.out.println("Бот успешно зарегистрирован");
    }
}