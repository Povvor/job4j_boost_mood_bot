package ru.job4j.bmb.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mb_advice")
public class Advice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private Boolean forGood;

    public Advice() {
    }

    public Advice(String text, Boolean forGood) {
        this.text = text;
        this.forGood = forGood;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getForGood() {
        return forGood;
    }

    public void setForGood(Boolean forGood) {
        this.forGood = forGood;
    }
}
