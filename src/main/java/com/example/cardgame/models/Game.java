package com.example.cardgame.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.*;

@RedisHash("Game")
public class Game {
    @Id
    @Getter
    private String id;
    @Getter
    private String name;
    @Setter
    @Getter
    private Boolean isGameStarted = false;
    @Getter
    @Indexed
    private Map<String, String> currentPlayers;

    @Getter
    private List<Card> gameDeck;

    public void AddPlayer(User player) {
        currentPlayers.put(player.getId(), player.getName());
    }

    public void RemovePlayer(User player) {
        currentPlayers.remove(player);
    }

    public void RemoveCardFromDeck(Card card) {
        gameDeck.remove(card);
    }

    public Game(String name) {
        currentPlayers = new HashMap<>();
        this.name = name;
        gameDeck = Arrays.asList(
                new Card(2, "clubs_2"),
                new Card(3, "clubs_3"),
                new Card(4, "clubs_4"),
                new Card(5, "clubs_5"),
                new Card(6, "clubs_6"),
                new Card(7, "clubs_7"),
                new Card(8, "clubs_8"),
                new Card(9, "clubs_9"),
                new Card(10, "clubs_10"),
                new Card(11, "clubs_A"),
                new Card(12, "clubs_J"),
                new Card(13, "clubs_K"),
                new Card(14, "clubs_Q"),

                new Card(2, "diamonds_2"),
                new Card(3, "diamonds_3"),
                new Card(4, "diamonds_4"),
                new Card(5, "diamonds_5"),
                new Card(6, "diamonds_6"),
                new Card(7, "diamonds_7"),
                new Card(8, "diamonds_8"),
                new Card(9, "diamonds_9"),
                new Card(10, "diamonds_10"),
                new Card(11, "diamonds_A"),
                new Card(12, "diamonds_J"),
                new Card(13, "diamonds_K"),
                new Card(14, "diamonds_Q"),

                new Card(2, "hearts_2"),
                new Card(3, "hearts_3"),
                new Card(4, "hearts_4"),
                new Card(5, "hearts_5"),
                new Card(6, "hearts_6"),
                new Card(7, "hearts_7"),
                new Card(8, "hearts_8"),
                new Card(9, "hearts_9"),
                new Card(10, "hearts_10"),
                new Card(11, "hearts_A"),
                new Card(12, "hearts_J"),
                new Card(13, "hearts_K"),
                new Card(14, "hearts_Q"),

                new Card(2, "spades_2"),
                new Card(3, "spades_3"),
                new Card(4, "spades_4"),
                new Card(5, "spades_5"),
                new Card(6, "spades_6"),
                new Card(7, "spades_7"),
                new Card(8, "spades_8"),
                new Card(9, "spades_9"),
                new Card(10, "spades_10"),
                new Card(11, "spades_A"),
                new Card(12, "spades_J"),
                new Card(13, "spades_K"),
                new Card(14, "spades_Q"),

                new Card(15, "joker"),
                new Card(16, "joker")
        );
    }
}
