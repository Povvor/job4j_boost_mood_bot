package ru.job4j.bmb.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.job4j.bmb.model.Advice;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.fake.AdviceFakeRepository;
import ru.job4j.bmb.repository.fake.MoodLogFakeRepository;
import ru.job4j.bmb.repository.fake.UserFakeRepository;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class AdviceServiceTest {

    @Autowired
    AdviceService adviceService;

    @Autowired
    private MoodLogFakeRepository moodLogRepository;

    @Autowired
    private AdviceFakeRepository adviceRepository;

    @Autowired
    private UserFakeRepository userRepository;

    private final Mood testMoodGood = new Mood("Good", true);
    private final Mood testMoodBad = new Mood("Bad", false);
    private final User testUser1 = new User(1L, 100, 1);
    private final User testUser2 = new User(2L, 200, 2);
    private final  User testUser3 = new User(3L, 300, 3);

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        moodLogRepository.deleteAll();
        adviceRepository.deleteAll();
        userRepository.save(testUser1);
        userRepository.save(testUser2);
        userRepository.save(testUser3);
        adviceRepository.save(new Advice("1", true));
        adviceRepository.save(new Advice("2", true));
        adviceRepository.save(new Advice("3", false));
        adviceRepository.save(new Advice("4", false));
        moodLogRepository.save(new MoodLog(testUser1, testMoodGood, 100));
        moodLogRepository.save(new MoodLog(testUser2, testMoodGood, 100));
        moodLogRepository.save(new MoodLog(testUser3, testMoodBad, 100));
    }

    @Test
    void adviceUsersTestWhenAllUsersTrue() {
        adviceService.initGoodAndBad();
        var result = adviceService.adviceUsers();
        assertThat(result).hasSize(3);
    }

    @Test
    void adviceUsersTestWhenOneUserFalse() {
        adviceService.initGoodAndBad();
        testUser2.setAdvicesEnabled(false);
        var result = adviceService.adviceUsers();
        assertThat(result).hasSize(2);
    }

    @Test
    void adviceUserTest() {
        adviceService.initGoodAndBad();
        var result1 = adviceService.adviceUser(testUser1);
        var result2 = adviceService.adviceUser(testUser3);
        assertThat(result1.orElseThrow().getText()).isIn("1", "2");
        assertThat(result2.orElseThrow().getText()).isIn("3", "4");
    }

    @Test
    void switchTestTrueToFalse() {
        var result = adviceService.switchAdvice(testUser1).orElseThrow().getText();
        assertThat(result).isEqualTo("Служба Совета дня для вас отключена.");
        assertThat(testUser1.isAdvicesEnabled()).isFalse();
    }

    @Test
    void switchTest2FalseToTrue() {
        testUser1.setAdvicesEnabled(false);
        var result = adviceService.switchAdvice(testUser1).orElseThrow().getText();
        assertThat(result).isEqualTo("Служба Совета дня для вас теперь активна!!!");
        assertThat(testUser1.isAdvicesEnabled()).isTrue();
    }

    @Test
    void userLastMoodTestWhenGood() {
        boolean result = adviceService.isUserLastMoodGood(testUser1);
        assertThat(result).isTrue();
    }

    @Test
    void userLastMoodTestWhenBad() {
        boolean result = adviceService.isUserLastMoodGood(testUser3);
        assertThat(result).isFalse();
    }

}