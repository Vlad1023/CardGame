package com.example.cardgame.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void AddPlayer(User player){
        currentPlayers.put(player.getId(), player.getName());
    }

    public void RemovePlayer(User player){
        currentPlayers.remove(player);
    }

    public Game(String name) {
        currentPlayers = new HashMap<String, String>();
        this.name = name;
    }
}
