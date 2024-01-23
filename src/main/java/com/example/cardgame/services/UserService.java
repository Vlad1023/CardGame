package com.example.cardgame.services;

import com.example.cardgame.models.Game;
import com.example.cardgame.models.Move;
import com.example.cardgame.models.User;
import com.example.cardgame.repositories.MoveRepository;
import com.example.cardgame.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    MoveRepository moveRepository;
    public String GetUserOpponentId(Game game, User user){
        var opponentUserId = game.getCurrentPlayers().entrySet().stream().filter(x -> !x.getKey().equals(user.getId())).findFirst().get().getKey();
        return opponentUserId;
    }
    public void ResetUserAfterFinishedGame(User user) {
        Move currentMove = user.getCurrentMove();
        if (currentMove != null) {
            moveRepository.delete(currentMove);
        }
    }
}
