package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.bmb.model.Advice;
import ru.job4j.bmb.model.Mood;

import java.util.List;

public interface AdviceRepository extends CrudRepository<Advice, Long> {
    List<Advice> findAll();
}

