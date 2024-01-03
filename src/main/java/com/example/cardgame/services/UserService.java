package com.example.cardgame.services;

import com.example.cardgame.models.Game;
import com.example.cardgame.models.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public String GetUserOpponentId(Game game, User user){
        var opponentUserId = game.getCurrentPlayers().entrySet().stream().filter(x -> !x.getKey().equals(user.getId())).findFirst().get().getKey();
        return opponentUserId;
    }
}
