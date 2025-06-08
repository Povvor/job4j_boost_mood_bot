package ru.job4j.bmb.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;

@Service
public class BotCommandHandler implements BeanNameAware {
    void receive(Content content) {
        System.out.println(content);
    }

    @PostConstruct
    public void init() {
        System.out.println("BotCommandHandler is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("BotCommandHandler will be destroyed now.");
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("Имя:" + name);
    }
}