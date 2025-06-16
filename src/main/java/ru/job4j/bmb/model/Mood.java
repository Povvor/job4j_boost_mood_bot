package ru.job4j.bmb.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mb_mood")
public class Mood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private boolean good;

    public Mood() {
    }

    public Mood(String text, boolean good) {
        this.text = text;
        this.good = good;
    }

    public String getText() {
        return text;
    }

    public boolean isGood() {
        return good;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setGood(boolean good) {
        this.good = good;
    }
}