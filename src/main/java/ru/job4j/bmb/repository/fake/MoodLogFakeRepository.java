package ru.job4j.bmb.repository.fake;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.repository.MoodLogRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Profile("test")
@Repository
@Primary
public class MoodLogFakeRepository
        extends CrudRepositoryFake<MoodLog, Long>
        implements MoodLogRepository {
    public long id = 1;

    public List<MoodLog> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public List<MoodLog> findByUserId(Long userId) {
        return memory.values().stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Stream<MoodLog> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return memory.values().stream()
                .filter(moodLog -> moodLog.getUser().getId().equals(userId))
                .sorted(Comparator.comparing(MoodLog::getCreatedAt).reversed());
    }

    @Override
    public MoodLog save(MoodLog moodLog) {
        if (moodLog.getId() == null || moodLog.getId() == 0) {
            memory.put(id++, moodLog);
        } else {
            memory.put(moodLog.getId(), moodLog);
        }
        return moodLog;
    }

}
