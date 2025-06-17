package ru.job4j.bmb.logic;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.recomendations.RecommendationEngine;
import ru.job4j.bmb.repository.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MoodService {
    private final MoodLogRepository moodLogRepository;
    private final RecommendationEngine recommendationEngine;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final MoodRepository moodRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public MoodService(MoodLogRepository moodLogRepository,
                       RecommendationEngine recommendationEngine,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository,
                       MoodRepository moodRepository) {
        this.moodLogRepository = moodLogRepository;
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.moodRepository = moodRepository;
    }

    public Content choseMood(User user, Long moodId) {
        moodLogRepository.save(new MoodLog(user,
                moodRepository.findById(moodId).orElseThrow(),
                System.currentTimeMillis()));
        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }

    public Optional<Content> weekMoodLogCommand(long chatId, Long clientId) {
        User user = userRepository.findById(clientId).orElseThrow();
        long sevenDays = 7 * 24 * 60 * 60 * 1000;
        StringBuilder builder = new StringBuilder();
        Content content = new Content(chatId);
        for (MoodLog moodLog : moodLogRepository.findAll()) {
           long created = moodLog.getCreatedAt();
            if (moodLog.getUser() == user && System.currentTimeMillis() - created >= sevenDays)  {
                builder.append(formatter.format(Instant.ofEpochMilli(created)) + " " +  moodLog.getMood() + "\n");
            }
        }
        content.setText(builder.toString());
                return Optional.of(content);
    }

    public Optional<Content> monthMoodLogCommand(long chatId, Long clientId) {
        User user = userRepository.findById(clientId).orElseThrow();
        long thirtyDays = 30L * 24 * 60 * 60 * 1000;
        StringBuilder builder = new StringBuilder();
        Content content = new Content(chatId);
        for (MoodLog moodLog : moodLogRepository.findAll()) {
            long created = moodLog.getCreatedAt();
            if (moodLog.getUser() == user && System.currentTimeMillis() - created >= thirtyDays)  {
                builder.append(formatter.format(Instant.ofEpochMilli(created)))
                        .append(' ')
                        .append(moodLog.getMood())
                        .append("\n");
            }
        }
        content.setText(builder.toString());
        return Optional.of(content);
    }

    private String formatMoodLogs(List<MoodLog> logs, String title) {
        if (logs.isEmpty()) {
            return title + ":\nNo mood logs found.";
        }
        var sb = new StringBuilder(title + ":\n");
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(log.getCreatedAt()));
            sb.append(formattedDate).append(": ").append(log.getMood().getText()).append("\n");
        });
        return sb.toString();
    }

    public Optional<Content> awards(long chatId, Long clientId) {
        User user = userRepository.findById(clientId).orElseThrow();
        long thirtyDays = 30L * 24 * 60 * 60 * 1000;
        StringBuilder builder = new StringBuilder();
        Content content = new Content(chatId);
        builder.append("Ваши награды: \n");
        for (Achievement achievement : achievementRepository.findAll()) {
            if (achievement.getUser() == user)  {
                builder.append(achievement.getAward()).append("\n");
            }
        }
        content.setText(builder.toString());
        return Optional.of(content);
    }
}