package ru.job4j.bmb.logic;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.recomendations.RecommendationEngine;
import ru.job4j.bmb.repository.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MoodService {
    private final ReminderService reminderService;
    private final MoodLogRepository moodLogRepository;
    private final RecommendationEngine recommendationEngine;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final MoodRepository moodRepository;
    private final ApplicationEventPublisher publisher;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public MoodService(ReminderService reminderService, MoodLogRepository moodLogRepository,
                       RecommendationEngine recommendationEngine,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository,
                       MoodRepository moodRepository, ApplicationEventPublisher publisher) {
        this.reminderService = reminderService;
        this.moodLogRepository = moodLogRepository;
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.moodRepository = moodRepository;
        this.publisher = publisher;
    }

    public Content choseMood(User user, Long moodId) {
        if (didUserVoteToday(user)) {
            Content content = new Content(user.getChatId());
            content.setText("Вы уже выбирали настроение сегодня!");
            return content;
        }
        moodLogRepository.save(new MoodLog(user,
                moodRepository.findById(moodId).orElseThrow(),
                System.currentTimeMillis()));
        publisher.publishEvent(new UserEvent(this, user));
        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }

    public boolean didUserVoteToday(User user) {
        var startOfDay = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        return moodLogRepository.findAll().stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(user.getId()))
                .anyMatch(moodLog -> moodLog.getCreatedAt() >= startOfDay);
    }

    public Optional<Content> weekMoodLogCommand(long chatId, User user) {
        System.out.println("<UNK> <UNK> <UNK> <UNK> <UNK> <UNK> <UNK> <UNK> <UNK>");
        long sevenDays = 7L * 24 * 60 * 60 * 1000;
        Content content = new Content(chatId);

        var logs = moodLogRepository.findAll().stream()
                .filter(created -> System.currentTimeMillis() - created.getCreatedAt() <= sevenDays)
                .filter(userToFilter -> userToFilter.getUser().equals(user))
                .toList();

        content.setText(formatMoodLogs(logs, "Лог настроения за 7 дней."));
        return Optional.of(content);
    }

    public Optional<Content> monthMoodLogCommand(long chatId, User user) {
        long thirtyDays = 30L * 24 * 60 * 60 * 1000;
        Content content = new Content(chatId);

        var logs = moodLogRepository.findAll().stream()
                .filter(created -> System.currentTimeMillis() - created.getCreatedAt() <= thirtyDays)
                .filter(userToFilter -> userToFilter.getUser().equals(user))
                .toList();

        content.setText(formatMoodLogs(logs, "Лог настроения за 30 дней."));
        return Optional.of(content);
    }

    private String formatMoodLogs(List<MoodLog> logs, String title) {
        if (logs.isEmpty()) {
            return title + ":\nNo mood logs found.";
        }
        var sb = new StringBuilder(title + ":\n");
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochMilli(log.getCreatedAt()));
            sb.append(formattedDate).append(": ").append(log.getMood().getText()).append("\n");
        });
        return sb.toString();
    }

    public Optional<Content> awards(long chatId, Long clientId) {
        User user = userRepository.findById(clientId).orElseThrow();
        StringBuilder builder = new StringBuilder();
        Content content = new Content(chatId);
        builder.append("Ваши награды: \n");

        achievementRepository.findAll().stream()
                .filter(achievement -> achievement.getUser().equals(user))
                .map(Achievement::getAward)
                .forEach(award -> builder.append(award).append("\n"));

        content.setText(builder.toString());
        return Optional.of(content);
    }

    public long goodMoodStreak(MoodLogRepository moodLogRepository, User user) {
        var moodLogs = moodLogRepository.findAll();
        return moodLogs.stream()
                .filter(log -> log.getUser().equals(user))
                .sorted(Comparator.comparing(MoodLog::getCreatedAt).reversed())
                .takeWhile(log -> log.getMood().isGood())
                .count();
    }
}