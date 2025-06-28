package ru.job4j.bmb.repository.fake;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Profile("test")
@Repository
@Primary
public class UserFakeRepository extends CrudRepositoryFake<User, Long> implements UserRepository {

    private static long id = 0;

    public List<User> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public boolean existsByClientId(long clientId) {
        return false;
    }

    @Override
    public Optional<User> findByClientId(long clientId) {
        return Optional.empty();
    }

    @Override
    public User findByChatId(long chatId) {
        return null;
    }

    @Override
    public User save(User user) {
        if (user.getId() == null || user.getId() == 0) {
            memory.put(id++, user);
        } else {
            memory.put(user.getId(), user);
        }
        return user;
    }

}
