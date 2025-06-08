package ru.job4j.bmb.recomendations;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public class RecommendationEngine {

    @PostConstruct
    public void init() {
        System.out.println("RecommendationEngine is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("RecommendationEngine will be destroyed now.");
    }
}
