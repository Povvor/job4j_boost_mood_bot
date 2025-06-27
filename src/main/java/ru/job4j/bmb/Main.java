package ru.job4j.bmb;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.job4j.bmb.model.Advice;
import ru.job4j.bmb.model.Award;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodContent;
import ru.job4j.bmb.repository.AdviceRepository;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.repository.MoodContentRepository;
import ru.job4j.bmb.repository.MoodRepository;
import ru.job4j.bmb.services.TelegramBotService;

import java.util.ArrayList;

@EnableScheduling
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            var bot = ctx.getBean(TelegramBotService.class);
            try {
                bot.register();
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        };
    }

    @Bean
    CommandLineRunner loadDatabase(MoodRepository moodRepository,
                                   MoodContentRepository moodContentRepository,
                                   AwardRepository awardRepository,
                                   AdviceRepository adviceRepository) {
        return args -> {
            var moods = moodRepository.findAll();
            if (!moods.isEmpty()) {
                return;
            }
            var data = initData();
            moodRepository.saveAll(data.stream().map(MoodContent::getMood).toList());
            moodContentRepository.saveAll(data);
            var awards = initAwards();
            awardRepository.saveAll(awards);
            var advices = initAdvices();
            adviceRepository.saveAll(advices);

        };
    }

    private static ArrayList<Award> initAwards() {
        var awards = new ArrayList<Award>();
        awards.add(new Award("Смайлик дня", "За 1 день хорошего настроения.", 1));
        awards.add(new Award("Настроение недели", "За 7 последовательных дней хорошего или отличного настроения.", 7));
        awards.add(new Award("Бонусные очки", "За каждые 3 дня хорошего настроения.", 3));
        awards.add(new Award("Персонализированные рекомендации", "После 5 дней хорошего настроения.", 5));
        awards.add(new Award("Достижение 'Солнечный луч'", "За 10 дней непрерывного хорошего настроения.", 10));
        awards.add(new Award("Виртуальный подарок", "После 15 дней хорошего настроения.", 15));
        awards.add(new Award("Титул 'Лучезарный'", "За 20 дней хорошего или отличного настроения.", 20));
        awards.add(new Award("Доступ к премиум-функциям", "После 30 дней хорошего настроения.", 30));
        awards.add(new Award("Участие в розыгрыше призов", "За каждую неделю хорошего настроения.", 7));
        awards.add(new Award("Эксклюзивный контент", "После 25 дней хорошего настроения.", 25));
        awards.add(new Award("Награда 'Настроение месяца", "За поддержание хорошего или отличного настроения в течение целого месяца.", 30));
        awards.add(new Award("Физический подарок", "После 60 дней хорошего настроения.", 60));
        awards.add(new Award("Коучинговая сессия", "После 45 дней хорошего настроения.", 45));
        awards.add(new Award("Разблокировка мини-игр", "После 14 дней хорошего настроения.", 14));
        awards.add(new Award("Персональное поздравление", "За значимые достижения (например, 50 дней хорошего настроения).", 50));
        return  awards;
    }

    private static ArrayList<MoodContent> initData() {
        var data = new ArrayList<MoodContent>();
        data.add(new MoodContent(
                new Mood("Счастливейший на свете \uD83D\uDE0E", true),
                "Великолепно! Вы чувствуете себя на высоте. Продолжайте в том же духе."));
        data.add(new MoodContent(
                new Mood("Воодушевленное настроение \uD83C\uDF1F", true),
                "Потрясающе! Вы в состоянии внутреннего мира и гармонии."));
        data.add(new MoodContent(
                new Mood("В состоянии комфорта ☺️", true),
                "Отлично! Вы чувствуете себя уютно и спокойно."));
        data.add(new MoodContent(
                new Mood("Легкое волнение \uD83C\uDF88", true),
                "Замечательно! Немного волнения добавляет жизни краски."));
        data.add(new MoodContent(
                new Mood("Сосредоточенное настроение \uD83C\uDFAF", true),
                "Хорошо! Ваш фокус на высоте, используйте это время эффективно."));
        data.add(new MoodContent(
                new Mood("Тревожное настроение \uD83D\uDE1F", false),
                "Хорошо! Ваш фокус на высоте, используйте это время эффективно."));
        data.add(new MoodContent(
                new Mood("Разочарованное настроение \uD83D\uDE1E", false),
                "Бывает. Не позволяйте разочарованию сбить вас с толку, всё наладится."));
        data.add(new MoodContent(
                new Mood("Усталое настроение \uD83D\uDE34", false),
                "Похоже, вам нужен отдых. Позаботьтесь о себе и отдохните."));
        data.add(new MoodContent(
                new Mood("Вдохновенное настроение \uD83D\uDCA1", true),
                "Потрясающе! Вы полны идей и энергии для их реализации."));
        data.add(new MoodContent(
                new Mood("Раздраженное настроение \uD83D\uDE20", false),
                "Попробуйте успокоиться и найти причину раздражения, чтобы исправить ситуацию."));
        return data;
    }

    public static ArrayList<Advice> initAdvices() {
        var data = new ArrayList<Advice>();
        data.add(new Advice("Поделись настроением с другими — скажи что-то приятное близким или даже случайным людям. Позитив заразителен.", true));
        data.add(new Advice("Занимайся творчеством — попробуй что-то создать: нарисуй, напиши пост, сделай смешную фотку или приготовь что-то необычное.", true));
        data.add(new Advice("Запомни этот момент — сделай заметку, селфи или аудиозапись, чтобы потом вспомнить, что именно подняло тебе настроение.", true));
        data.add(new Advice("Сделай что-то сложное — сейчас проще браться за задачи, которые давно откладывал: используй подъем энергии с пользой.", true));
        data.add(new Advice("Побалуй себя — если давно хотел попробовать что-то новенькое или просто съесть вкусняшку, сейчас отличный момент!", true));
        data.add(new Advice("Сделай что-то маленькое — выполни простое действие: прими душ, убери стол, прогуляйся на 5 минут. Маленькие шаги помогают выбраться из апатии.", false));
        data.add(new Advice("Ограничь новости и соцсети — информационный шум легко усугубляет негатив. Лучше сделай цифровой детокс на пару часов.", false));
        data.add(new Advice("Поговори с кем-то, кому доверяешь — иногда даже короткий разговор помогает взглянуть на ситуацию иначе.", false));
        data.add(new Advice("Позаботься о теле — выпей воды, поспи, сделай растяжку или дыхательную практику. Тело и мозг связаны крепче, чем кажется.", false));
        data.add(new Advice("Не подавляй эмоции — позволь себе побыть в этом состоянии, не ругай себя, не требуй радости здесь и сейчас.", false));
        return data;
    }
}
