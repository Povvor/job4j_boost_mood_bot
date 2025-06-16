package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.bmb.model.Achievement;
import java.util.List;

public interface AchievementRepository extends CrudRepository<Achievement, Long> {
    List<Achievement> findAll();
}