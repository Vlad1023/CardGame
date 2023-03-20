package com.example.cardgame.models;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

@RedisHash("Game")
public class Game {
    @Id
    @Getter
    private String id;
    @Getter
    private String name;
    @Getter
    private List<String> currentPlayers;

    public void AddPlayer(String player){
        currentPlayers.add(player);
    }

    public void RemovePlayer(String player){
        currentPlayers.remove(player);
    }

    public Game(String name) {
        currentPlayers = new ArrayList<>();
        this.name = name;
    }
}
