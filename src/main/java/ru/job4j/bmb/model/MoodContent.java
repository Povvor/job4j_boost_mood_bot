package ru.job4j.bmb.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mb_mood_content")
public class MoodContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mood_id")
    private Mood mood;

    private String text;

    public MoodContent() {
    }

    public MoodContent(Mood mood, String text) {
        this.mood = mood;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

