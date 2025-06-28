package ru.job4j.bmb.services;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.conditions.OnDevCondition;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.logic.AdviceService;
import ru.job4j.bmb.logic.ReminderService;

@Service
@Conditional(OnDevCondition.class)
@Profile("test")
@Primary
public class TelegramBotServiceDev extends TelegramBotService implements SentContent {

    public TelegramBotServiceDev(BotCommandHandler handler, ReminderService reminderService, AdviceService adviceService) {
        super("0", "0", handler, reminderService, adviceService);
    }

    @Override
    public void sent(Content content) {
        if (content.getAudio() != null) {
            System.out.println("Обработка аудио недоступна в консольном режиме.");
        } else if (content.getMarkup() != null) {
            var keyboard = content.getMarkup().getKeyboard();
            keyboard.forEach(button -> System.out.println(button.toString()));
        } else if (content.getPhoto() != null) {
            System.out.println("Обработка фото недоступна в консольном режиме.");
        } else {
            System.out.println(content.getText());
        }

    }

    @Override
    public void register() {
        System.out.println("Тестовый бот запущен.");
}
}