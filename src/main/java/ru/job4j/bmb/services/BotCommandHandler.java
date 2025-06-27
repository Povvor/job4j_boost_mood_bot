package ru.job4j.bmb.services;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.logic.MoodService;
import ru.job4j.bmb.logic.ReminderService;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.UserRepository;

import java.util.Optional;

@Service
public class BotCommandHandler {
    private final UserRepository userRepository;
    private final MoodService moodService;
    private final TgUI tgUI;
    private final ReminderService reminderService;

    public BotCommandHandler(UserRepository userRepository,
                             MoodService moodService,
                             TgUI tgUI, ReminderService reminderService) {
        this.userRepository = userRepository;
        this.moodService = moodService;
        this.tgUI = tgUI;
        this.reminderService = reminderService;
    }

    Optional<Content> commands(Message message) {
        System.out.println("Commands received");
        System.out.println(message.getText());
        return switch (message.getText()) {
            case "/info", "/help" -> info(userRepository.findByChatId(message.getChatId()));
            case "/start" -> handleStartCommand(message.getChatId(), message.getFrom().getId());
            case "/week_mood_log" -> moodService.weekMoodLogCommand(message.getChatId(), message.getFrom().getId());
            case "/month_mood_log" -> moodService.monthMoodLogCommand(message.getChatId(), message.getFrom().getId());
            case "/award" -> moodService.awards(message.getChatId(), message.getFrom().getId());
            case "/switch_advice" -> reminderService.switchAdvice(userRepository.findByChatId(message.getChatId()));
            case "/daily_advice" -> reminderService.adviceUser(userRepository.findByChatId(message.getChatId()));
            default -> Optional.empty();
        };
    }

    Optional<Content> handleCallback(CallbackQuery callback) {
        var moodId = Long.valueOf(callback.getData());
        var user = userRepository.findByClientId(callback.getFrom().getId());
        return user.map(value -> moodService.choseMood(value, moodId));
    }

    private Optional<Content> info(User user) {
        Content content = new Content(user.getChatId());
        content.setText("""
                /start: Выбрать настроекние!\s
                /week_mood_log: Получить лог настроения за неделю\s
                /month_mood_log: Получить лог настроения за месяц\s
                /award: Получить список достижений\s
                /daily_advice: получть совет""");
        return Optional.of(content);
    }

    private Optional<Content> handleStartCommand(long chatId, Long clientId) {
        var user = new User();
        user.setClientId(clientId);
        user.setChatId(chatId);
        System.out.println(user.getClientId());
        if (userRepository.existsByClientId(user.getClientId())) {
            System.out.println("User уже есть!");
        } else {
            System.out.println(userRepository.save(user));
        }
        var content = new Content(user.getChatId());
        content.setText("Как настроение?");
        content.setMarkup(tgUI.buildButtons());
        return Optional.of(content);
    }

    void receive(Content content) {
        System.out.println(content);
    }

    @PostConstruct
    public void init() {
        System.out.println("BotCommandHandler is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("BotCommandHandler will be destroyed now.");
    }
}
