package com.example.cardgame.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Game")
public class Move {
    @Id
    @Getter
    private String id;
    @Getter
    private Card card;
    public Move(Card card) {
        this.card = card;
    }
}
