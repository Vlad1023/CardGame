package com.example.cardgame.models;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@RedisHash("User")
public class User {
    @Id
    @Getter
    private String id;
    @Getter
    private String name;
    @Getter
    private LinkedList<Card> currentCards;

    public User(String name) {
        this.name = name;
        this.currentCards = new LinkedList<>();
    }

    public void addCard(Card card) {
        currentCards.add(card);
    }
}
