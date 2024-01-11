package com.example.cardgame.models;

import com.example.cardgame.GameStatusAfterMove;
import lombok.Getter;
import lombok.Setter;
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
    @Getter
    private int currentVictories = 0;
    @Getter
    @Setter
    private Move currentMove;
    @Getter
    @Setter
    private GameStatusAfterMove gameStatusAfterLastMove;
    public int getCurrentNumberOfCards() {
        return currentCards.size();
    }


    public User(String name) {
        this.name = name;
        this.currentCards = new LinkedList<>();
    }
    public void addCard(Card card) {
        currentCards.add(card);
    }
    public void addVictory() {
        currentVictories++;
    }
    public Card removeFirstCard() {
        if (!currentCards.isEmpty()) {
            return currentCards.removeFirst();
        }
        return null;
    }
}
