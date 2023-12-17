package com.example.cardgame.models;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Card")
public class Card {
    @Id
    @Getter
    private String id;
    @Getter
    private int rank;
    @Getter
    private String representation;

    public Card() {}

    public Card(int rank, String representation) {
        this.rank = rank;
        this.representation = representation;
    }

    public Card(Card card) {
        this.rank = card.getRank();
        this.representation = card.getRepresentation();
    }
}
