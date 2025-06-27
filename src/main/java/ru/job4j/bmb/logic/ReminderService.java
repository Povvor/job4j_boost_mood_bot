package ru.job4j.bmb.logic;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.exception.MoodLogIsEmpty;
import ru.job4j.bmb.model.Advice;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.AdviceRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.UserRepository;
import ru.job4j.bmb.services.TgUI;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ReminderService {
    private final MoodLogRepository moodLogRepository;
    private final TgUI tgUI;
    private final List<MoodLog> moodLogList;
    private final UserRepository userRepository;
    private final AdviceRepository adviceRepository;
    Random random = new Random();

    private final List<String> goodAdvices;
    private final List<String> badAdvices;

    public ReminderService(MoodLogRepository moodLogRepository,
                           TgUI tgUI,
                           UserRepository userRepository,
                           AdviceRepository adviceRepository) {
        this.moodLogRepository = moodLogRepository;
        this.tgUI = tgUI;
        this.moodLogList = moodLogRepository.findAll();
        this.userRepository = userRepository;
        this.adviceRepository = adviceRepository;
        goodAdvices = adviceRepository.findAll().stream().filter(Advice::getForGood).map(Advice::getText).toList();
        badAdvices = adviceRepository.findAll().stream().filter(Predicate.not(Advice::getForGood)).map(Advice::getText).toList();
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
        var endOfDay = LocalDate.now()
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 1;
        var users = findUsersWhoDidNotVoteToday(startOfDay, endOfDay);
        for (var user : users) {
            contents.add(remindUser(user).orElseThrow());
        }
        return contents;
    }

    public List<Content> adviceUsers() {
        List<Content> contents = new  ArrayList<>();
        List<String> goodAdvices = adviceRepository.findAll().stream().filter(Advice::getForGood).map(Advice::getText).toList();
        List<String> badAdvices = adviceRepository.findAll().stream().filter(Predicate.not(Advice::getForGood)).map(Advice::getText).toList();
        for (var user : userRepository.findAll()) {
            if (user.isAdvicesEnabled()) {
                contents.add(adviceUser(user).orElseThrow());
            }
        }
        return contents;
    }

    public Optional<Content> adviceUser(User user) {
        var content = new Content(user.getChatId());
        try {
            if (isUserLastMoodGood(user)) {
                int randomIndex = random.nextInt(goodAdvices.size());
                content.setText(goodAdvices.get(randomIndex));
            } else {
                int randomIndex = random.nextInt(badAdvices.size());
                content.setText(badAdvices.get(randomIndex));
            }
        } catch (MoodLogIsEmpty e) {
            content = remindUser(user).orElseThrow();
        }
        return Optional.of(content);
    }

    public Optional<Content> switchAdvice(User user) {
        var content = new Content(user.getChatId());
        user.setAdvicesEnabled(!user.isAdvicesEnabled());
        if (user.isAdvicesEnabled()) {
            content.setText("Служба Совета дня для вас теперь активна!!!");
        } else {
            content.setText("Служба Совета дня для вас отключена.");
        }
        return Optional.of(content);
    }

    public boolean isUserLastMoodGood(User user) {
        return moodLogRepository.findByUserId(user.getId()).stream()
                .sorted(Comparator.comparing(MoodLog::getCreatedAt).reversed())
                .map(MoodLog::getMood)
                .map(Mood::isGood)
                .findFirst().orElseThrow(MoodLogIsEmpty::new);
    }

    public List<User> findUsersWhoDidNotVoteToday(long startOfDay, long endOfDay) {
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

    public List<MoodLog> findMoodLogsForWeek(Long userId, long weekStart) {
        return moodLogList.stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .filter(moodLog -> moodLog.getCreatedAt() >= weekStart)
                .collect(Collectors.toList());
    }

    public List<MoodLog> findMoodLogsForMonth(Long userId, long monthStart) {
        return moodLogList.stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .filter(moodLog -> moodLog.getCreatedAt() >= monthStart)
                .collect(Collectors.toList());
    }
}

