package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;

import java.util.List;
import java.util.stream.Stream;

public interface MoodLogRepository extends CrudRepository<MoodLog, Long> {
    List<MoodLog> findAll();

    List<MoodLog> findByUserId(Long userId);

    Stream<MoodLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<User> findUsersWhoDidNotVoteToday(long startOfDay, long endOfDay);

    List<MoodLog> findMoodLogsForWeek(Long userId, long weekStart);

    List<MoodLog> findMoodLogsForMonth(Long userId, long monthStart);
}

