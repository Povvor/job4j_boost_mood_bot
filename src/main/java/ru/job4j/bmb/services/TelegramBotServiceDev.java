package ru.job4j.bmb.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.job4j.bmb.conditions.OnDevCondition;
import ru.job4j.bmb.conditions.OnProdCondition;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.exception.SentContentException;
import ru.job4j.bmb.repository.MoodContentRepository;
import ru.job4j.bmb.repository.UserRepository;

import java.util.Scanner;

@Service
@Conditional(OnDevCondition.class)
@Profile("test")
@Primary
public class TelegramBotServiceDev extends TelegramBotService implements SentContent {

    public TelegramBotServiceDev(BotCommandHandler handler, UserRepository userRepository, TgUI tgUI, MoodContentRepository moodRepository) {
        super("0", "0", handler, userRepository, tgUI, moodRepository);
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
    public void register() throws TelegramApiException {
        System.out.println("Тестовый бот запущен.");
}
}