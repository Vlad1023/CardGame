package com.example.cardgame.models;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

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
    @Indexed
    private List<User> currentPlayers;

    public void AddPlayer(User player){
        currentPlayers.add(player);
    }

    public void RemovePlayer(User player){
        currentPlayers.remove(player);
    }

    public Game(String name) {
        currentPlayers = new ArrayList<>();
        this.name = name;
    }
}
