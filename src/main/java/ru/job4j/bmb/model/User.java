package ru.job4j.bmb.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "mb_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id")
    private long clientId;

    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "advicesEnabled")
    private boolean advicesEnabled = true;

    public User() {
    }

    public boolean isAdvicesEnabled() {
        return advicesEnabled;
    }

    public void setAdvicesEnabled(boolean advicesEnabled) {
        this.advicesEnabled = advicesEnabled;
    }

    public User(Long id, long clientId, long chatId) {
        this.id = id;
        this.clientId = clientId;
        this.chatId = chatId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public Long getId() {
        return id;
    }

    public long getClientId() {
        return clientId;
    }

    public long getChatId() {
        return chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return clientId == user.clientId && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientId);
    }
}
