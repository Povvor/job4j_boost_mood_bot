package ru.job4j.bmb.logic;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public class AchievementService {
    @PostConstruct
    public void init() {
        System.out.println("AchievementService is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("AchievementService will be destroyed now.");
    }
}
