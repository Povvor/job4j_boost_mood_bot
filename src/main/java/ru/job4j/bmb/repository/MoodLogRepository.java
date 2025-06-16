package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.bmb.model.MoodLog;
import java.util.List;

public interface MoodLogRepository extends CrudRepository<MoodLog, Long> {
    List<MoodLog> findAll();
}
