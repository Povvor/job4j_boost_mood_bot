package ru.job4j.bmb.repository.fake;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.repository.AchievementRepository;

import java.util.ArrayList;
import java.util.List;

@Profile("test")
@Repository
@Primary
public class AchievementFakeRepository extends CrudRepositoryFake<Achievement, Long> implements AchievementRepository {

    private static long id = 0;

    public List<Achievement> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public Achievement save(Achievement achievement) {
        if (achievement.getId() == null || achievement.getId() == 0) {
            memory.put(id++, achievement);
        } else {
            memory.put(achievement.getId(), achievement);
        }
        return achievement;
    }

}
