package ru.job4j.bmb.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.fake.AchievementFakeRepository;
import ru.job4j.bmb.repository.fake.MoodLogFakeRepository;
import ru.job4j.bmb.repository.fake.UserFakeRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class ReminderServiceTest {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private MoodLogFakeRepository moodLogRepository;

    @Autowired
    private AchievementFakeRepository achievementRepository;

    @Autowired
    private UserFakeRepository userRepository;

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
        userRepository.save(testUser1);
        userRepository.save(testUser2);
        userRepository.save(testUser3);
        moodLogRepository.save(new MoodLog(testUser1, testMoodGood, calculateTime(0)));
        moodLogRepository.save(new MoodLog(testUser2, testMoodGood, calculateTime(1)));
        moodLogRepository.save(new MoodLog(testUser3, testMoodBad, calculateTime(1)));
        moodLogRepository.save(new MoodLog(testUser1, testMoodGood, calculateTime(1)));
        moodLogRepository.save(new MoodLog(testUser2, testMoodBad, calculateTime(2)));
        moodLogRepository.save(new MoodLog(testUser1, testMoodGood, calculateTime(2)));
        moodLogRepository.save(new MoodLog(testUser1, testMoodBad, calculateTime(3)));
    }

    @Test
    void remindUserTest() {
        var result = reminderService.remindUser(testUser1);
        assertThat(result.orElseThrow().getText()).isEqualTo("Как настроение?");
        assertThat(result.orElseThrow().getChatId()).isEqualTo(testUser1.getChatId());
    }

    @Test
    void remindUsersTest() {
        var result = reminderService.remindUsers().stream().map(Content::getChatId).toList();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(testUser1.getChatId())).isFalse();
        assertThat(result.contains(testUser2.getChatId())).isTrue();
        assertThat(result.contains(testUser3.getChatId())).isTrue();
    }

    @Test
    void usersWhoDidNotVoteTodayTest() {
        var startOfDay = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        var result = reminderService.findUsersWhoDidNotVoteToday(startOfDay);
        assertThat(result).hasSize(2);
    }
}
