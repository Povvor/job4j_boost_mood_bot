package ru.job4j.bmb.logic;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.exception.MoodLogIsEmpty;
import ru.job4j.bmb.model.Advice;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.AdviceRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.UserRepository;

import java.util.*;
import java.util.function.Predicate;

@Service
public class AdviceService {

    private final AdviceRepository adviceRepository;
    private final UserRepository userRepository;
    private final MoodLogRepository moodLogRepository;
    private final ReminderService reminderService;
    Random random = new Random();
    private  List<Advice> goodAdvices = new ArrayList<>();
    private  List<Advice> badAdvices = new ArrayList<>();

    public AdviceService(AdviceRepository adviceRepository, UserRepository userRepository, MoodLogRepository moodLogRepository, ReminderService reminderService) {
        this.adviceRepository = adviceRepository;
        this.userRepository = userRepository;
        this.moodLogRepository = moodLogRepository;
        this.reminderService = reminderService;
        initGoodAndBad();
    }

    /**
     * Метод, разделяет репозиторий советов, на две коллекции
     * для хорошего настроения и для плохого.
     */
    public void initGoodAndBad() {
        goodAdvices = adviceRepository.findAll().stream().filter(Advice::getForGood).toList();
        badAdvices = adviceRepository.findAll().stream().filter(Predicate.not(Advice::getForGood)).toList();
    }

    /**
     * Метод фильтрует всех пользователей по полю isAdvicesEnabled
     * Затем создается по объекту Content на каждого пользователя
     * который не отключил рассылку.
     * Собирается коллекция объектов Content
     * Которые далее будут отправлены пользователям.
     */
    public List<Content> adviceUsers() {
        List<Content> contents = new ArrayList<>();
        for (var user : userRepository.findAll()) {
            if (user.isAdvicesEnabled()) {
                System.out.println(user.getId());
                contents.add(adviceUser(user).orElseThrow());
            }
        }
        return contents;
    }

    /**
     * Метод принимает на вход пользователя,
     * определяется его последнее настроение,
     * хорошее или плохое, дальше происходит случайный
     * выбор сообщения из соответствующей группы.
     * В случае пустого лога настроений у пользователя,
     * ему будет предложено выбрать настроение.
     * @param user Пользователь для которого создается Content
     */
    public Optional<Content> adviceUser(User user) {
        var content = new Content(user.getChatId());
        try {
            if (isUserLastMoodGood(user)) {
                int randomIndex = random.nextInt(goodAdvices.size());
                content.setText(goodAdvices.get(randomIndex).getText());
            } else {
                int randomIndex = random.nextInt(badAdvices.size());
                content.setText(badAdvices.get(randomIndex).getText());
            }
        } catch (MoodLogIsEmpty e) {
            content = reminderService.remindUser(user).orElseThrow();
        }
        return Optional.of(content);
    }

    /**
     * Метод переключает поле isAdvicesEnabled для конкретного юзера,
     * выводя ему сообщение о статусе службы совета дня.
     * @param user Пользователь, который решил изменить статус службы совета дня для него.
     */
    public Optional<Content> switchAdvice(User user) {
        var content = new Content(user.getChatId());
        user.setAdvicesEnabled(!user.isAdvicesEnabled());
        if (user.isAdvicesEnabled()) {
            content.setText("Служба Совета дня для вас теперь активна!!!");
        } else {
            content.setText("Служба Совета дня для вас отключена.");
        }
        userRepository.save(user);
        return Optional.of(content);

    }

    /**
     * Метод определяет последнее настроение пользователя, хорошее или плохое.
     * @param user Пользователь для которого производится расчет.
     */
    public boolean isUserLastMoodGood(User user) {
        return moodLogRepository.findByUserId(user.getId()).stream()
                .sorted(Comparator.comparing(MoodLog::getCreatedAt).reversed())
                .map(MoodLog::getMood)
                .map(Mood::isGood)
                .findFirst().orElseThrow(MoodLogIsEmpty::new);
    }
}
