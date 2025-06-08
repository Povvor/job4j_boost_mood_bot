package ru.job4j.bmb.logic;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public class ReminderService {

    @PostConstruct
    public void init() {
        System.out.println("ReminderService is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("ReminderService will be destroyed now.");
    }
}
