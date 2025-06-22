package ru.job4j.bmb.logic;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.fake.AchievementFakeRepository;
import ru.job4j.bmb.repository.fake.MoodLogFakeRepository;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class AchievementServiceTest {

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private MoodLogFakeRepository moodLogRepository;

    @Autowired
    private AchievementFakeRepository achievementRepository;

    private final Mood testMoodGood = new Mood("Good", true);
    private final Mood testMoodBad = new Mood("Bad", false);
    private final User testUser1 = new User(100, 1);
    private final User testUser2 = new User(200, 2);

    private static long calculateTime(int minusDays) {
        return LocalDateTime.now()
                .minusDays(minusDays)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    @Test
    void whenNoStreakNotifyTest() {
        moodLogRepository.deleteAll();
        achievementRepository.deleteAll();
        moodLogRepository.save(new MoodLog(testUser1, testMoodBad, calculateTime(0)));
        moodLogRepository.save(new MoodLog(testUser2, testMoodGood, calculateTime(0)));
        List<Content> result = achievementService.grantAndNotify(testUser1);
        assertThat(result).hasSize(0);
    }

    @Test
    void whenNoStreakGrantTest() {
        moodLogRepository.deleteAll();
        achievementRepository.deleteAll();
        moodLogRepository.save(new MoodLog(testUser1, testMoodBad, calculateTime(0)));
        moodLogRepository.save(new MoodLog(testUser2, testMoodGood, calculateTime(0)));
        achievementService.grantAndNotify(testUser1);
        var result = achievementRepository.findAll().size();
        assertThat(result).isEqualTo(0);
    }

    @Test
    void whenMoodGoodStreak1DayNotifyTest() {
        moodLogRepository.deleteAll();
        achievementRepository.deleteAll();
        moodLogRepository.save(new MoodLog(testUser1, testMoodGood, calculateTime(0)));
        moodLogRepository.save(new MoodLog(testUser2, testMoodBad, calculateTime(0)));
        List<Content> result = achievementService.grantAndNotify(testUser1);
        assertThat(result).hasSize(1);
    }

    @Test
    void whenMoodGoodStreak1DayGrantTest() {
        moodLogRepository.deleteAll();
        achievementRepository.deleteAll();
        moodLogRepository.save(new MoodLog(testUser1, testMoodGood, calculateTime(0)));
        moodLogRepository.save(new MoodLog(testUser2, testMoodBad, calculateTime(0)));
        achievementService.grantAndNotify(testUser1);
        var result = achievementRepository.findAll().size();
        assertThat(result).isEqualTo(1);
    }

    @Test
    void whenMoodGoodStreak3DaysNotifyTest() {
        moodLogRepository.deleteAll();
        achievementRepository.deleteAll();

        moodLogRepository.save(new MoodLog(testUser1, testMoodGood, calculateTime(0)));
        moodLogRepository.save(new MoodLog(testUser2, testMoodGood, calculateTime(0)));
        moodLogRepository.save(new MoodLog(testUser1, testMoodGood, calculateTime(1)));
        moodLogRepository.save(new MoodLog(testUser2, testMoodBad, calculateTime(1)));
        moodLogRepository.save(new MoodLog(testUser1, testMoodGood, calculateTime(2)));
        moodLogRepository.save(new MoodLog(testUser1, testMoodBad, calculateTime(3)));

        List<Content> result = achievementService.grantAndNotify(testUser1);
        assertThat(result).hasSize(2);
    }

    @Test
    void whenMoodGoodStreak3DaysGrantTest() {
        moodLogRepository.deleteAll();
        achievementRepository.deleteAll();

        moodLogRepository.save(new MoodLog(testUser1, testMoodGood, calculateTime(0)));
        moodLogRepository.save(new MoodLog(testUser2, testMoodGood, calculateTime(0)));
        moodLogRepository.save(new MoodLog(testUser1, testMoodGood, calculateTime(1)));
        moodLogRepository.save(new MoodLog(testUser2, testMoodBad, calculateTime(1)));
        moodLogRepository.save(new MoodLog(testUser1, testMoodGood, calculateTime(2)));
        moodLogRepository.save(new MoodLog(testUser1, testMoodBad, calculateTime(3)));

        achievementService.grantAndNotify(testUser1);
        var result = achievementRepository.findAll().size();
        assertThat(result).isEqualTo(2);
    }

}