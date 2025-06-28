package ru.job4j.bmb.logic;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.UserRepository;
import ru.job4j.bmb.services.TgUI;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class ReminderService {
    private final MoodLogRepository moodLogRepository;
    private final TgUI tgUI;
    private final UserRepository userRepository;

    public ReminderService(MoodLogRepository moodLogRepository,
                           TgUI tgUI,
                           UserRepository userRepository) {
        this.moodLogRepository = moodLogRepository;
        this.tgUI = tgUI;
        this.userRepository = userRepository;
    }

    public Optional<Content> remindUser(User user) {
        var content = new Content(user.getChatId());
        content.setText("Как настроение?");
        content.setMarkup(tgUI.buildButtons());
        return Optional.of(content);
    }

    public List<Content> remindUsers() {
        List<Content> contents = new  ArrayList<>();
        var startOfDay = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        var users = findUsersWhoDidNotVoteToday(startOfDay);
        for (var user : users) {
            contents.add(remindUser(user).orElseThrow());
        }
        return contents;
    }

    public List<User> findUsersWhoDidNotVoteToday(long startOfDay) {
        List<User> users = new ArrayList<>();
        for (var user : userRepository.findAll()) {
            boolean did = moodLogRepository.findAll().stream()
                    .filter(moodLog -> moodLog.getUser().getId().equals(user.getId()))
                    .anyMatch(moodLog -> moodLog.getCreatedAt() >= startOfDay);
            if (!did) {
                users.add(user);
            }
        }
        return users;
    }
}

