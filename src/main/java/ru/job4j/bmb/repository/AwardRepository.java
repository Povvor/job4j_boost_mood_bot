package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.bmb.model.Award;
import java.util.List;

public interface AwardRepository extends CrudRepository<Award, Long> {
    List<Award> findAll();
}