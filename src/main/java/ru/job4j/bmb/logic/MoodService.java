package ru.job4j.bmb.logic;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public class MoodService {
    @PostConstruct
    public void init() {
        System.out.println("MoodService is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("MoodService will be destroyed now.");
    }
}
