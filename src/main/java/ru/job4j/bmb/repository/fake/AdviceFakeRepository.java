package ru.job4j.bmb.repository.fake;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.bmb.model.Advice;
import ru.job4j.bmb.repository.AdviceRepository;

import java.util.ArrayList;
import java.util.List;

@Profile("test")
@Repository
@Primary
public class AdviceFakeRepository extends CrudRepositoryFake<Advice, Long> implements AdviceRepository {
    private static long id = 0;

    public List<Advice> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public Advice save(Advice advice) {
        if (advice.getId() == null || advice.getId() == 0) {
            memory.put(id++, advice);
        } else {
            memory.put(advice.getId(), advice);
        }
        return advice;
    }

}
