package ru.job4j.bmb.logic;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.Award;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.services.TelegramBotService;

import java.util.List;
import java.util.function.Predicate;

@Component
public class AchievementService implements ApplicationListener<UserEvent> {

    TelegramBotService telegramBotService;
    MoodService moodService;
    MoodLogRepository moodLogRepository;
    AwardRepository awardRepository;
    AchievementRepository achievementRepository;

    public AchievementService(TelegramBotService telegramBotService,
                              MoodService moodService,
                              MoodLogRepository moodLogRepository,
                              AwardRepository awardRepository,
                              AchievementRepository achievementRepository) {
        this.telegramBotService = telegramBotService;
        this.moodService = moodService;
        this.moodLogRepository = moodLogRepository;
        this.awardRepository = awardRepository;
        this.achievementRepository = achievementRepository;
    }

    @Transactional
    @Override
    public void onApplicationEvent(UserEvent event) {
        var user = event.getUser();
        var awards = awardsToAchieve(user);

        for (Award achievement : awards) {
            achievementRepository.save(new Achievement(System.currentTimeMillis(), user, achievement));
            Content content = new Content(user.getChatId());
            String text = ("У вас новое достижение!!!"
                    + "\n" + achievement.getTitle()
                    + "\n" + achievement.getDescription());
            content.setText(text);
            telegramBotService.sent(content);
        }
    }

    public List<Award> awardsToAchieve(User user) {
        var streak = moodService.goodMoodStreak(moodLogRepository, user);
        var allAwards = awardRepository.findAll();
        var userAwards = achievementRepository.findAll().stream()
                .filter(achievement -> achievement.getUser().equals(user))
                .map(Achievement :: getAward)
                .toList();
        return allAwards.stream()
                .filter(award -> award.getDays() <= streak)
                .filter(Predicate.not(userAwards::contains))
                .toList();
    }

    @PostConstruct
    public void init() {
        System.out.println("AchievementService is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("AchievementService will be destroyed now.");
    }
}
