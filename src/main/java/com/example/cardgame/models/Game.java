package com.example.cardgame.models;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private List<String> currentPlayers;

    public void AddPlayer(String player){
        currentPlayers.add(player);
    }

    public void RemovePlayer(String player){
        currentPlayers.remove(player);
    }

    public Game() {
        currentPlayers = new ArrayList<>();

    }
}
