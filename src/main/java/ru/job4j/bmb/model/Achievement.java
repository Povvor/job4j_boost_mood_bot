package ru.job4j.bmb.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mb_achievement")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long createAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "award_id")
    private Award award;
    // getters, setters, hashCode, equals
}
