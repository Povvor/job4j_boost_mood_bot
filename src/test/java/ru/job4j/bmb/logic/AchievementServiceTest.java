package ru.job4j.bmb.logic;


import org.junit.jupiter.api.BeforeEach;
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
    private final User testUser1 = new User(1L, 100, 1);
    private final User testUser2 = new User(2L, 200, 2);
    private final  User testUser3 = new User(3L, 300, 3);

    private static long calculateTime(int minusDays) {
        return LocalDateTime.now()
                .minusDays(minusDays)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    @BeforeEach
    void setUp() {
        moodLogRepository.deleteAll();
        achievementRepository.deleteAll();
        moodLogRepository.save(new MoodLog(testUser1, testMoodGood, calculateTime(0)));
        moodLogRepository.save(new MoodLog(testUser2, testMoodGood, calculateTime(0)));
        moodLogRepository.save(new MoodLog(testUser3, testMoodBad, calculateTime(0)));
        moodLogRepository.save(new MoodLog(testUser1, testMoodGood, calculateTime(1)));
        moodLogRepository.save(new MoodLog(testUser2, testMoodBad, calculateTime(1)));
        moodLogRepository.save(new MoodLog(testUser1, testMoodGood, calculateTime(2)));
        moodLogRepository.save(new MoodLog(testUser1, testMoodBad, calculateTime(3)));

    }

    @Test
    void whenNoStreakNotifyTest() {
        List<Content> result = achievementService.grantAndNotify(testUser3);
        assertThat(result).hasSize(0);
    }

    @Test
    void whenNoStreakGrantTest() {
        achievementService.grantAndNotify(testUser3);
        var result = achievementRepository.findAll().size();
        assertThat(result).isEqualTo(0);
    }

    @Test
    void whenMoodGoodStreak1DayNotifyTest() {
        List<Content> result = achievementService.grantAndNotify(testUser2);
        assertThat(result).hasSize(1);
    }

    @Test
    void whenMoodGoodStreak1DayGrantTest() {
        achievementService.grantAndNotify(testUser2);
        var result = achievementRepository.findAll().size();
        assertThat(result).isEqualTo(1);
    }

    @Test
    void whenMoodGoodStreak3DaysNotifyTest() {
        List<Content> result = achievementService.grantAndNotify(testUser1);
        assertThat(result).hasSize(2);
    }

    @Test
    void whenMoodGoodStreak3DaysGrantTest() {
        achievementService.grantAndNotify(testUser1);
        var result = achievementRepository.findAll().size();
        assertThat(result).isEqualTo(2);
    }

}