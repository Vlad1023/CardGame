package com.example.cardgame.services;

import com.example.cardgame.GameStatusAfterMove;
import com.example.cardgame.models.Card;
import com.example.cardgame.models.Game;
import com.example.cardgame.models.Move;
import com.example.cardgame.models.User;
import com.example.cardgame.repositories.GameRepository;
import com.example.cardgame.repositories.UserRepository;
import com.example.cardgame.validators.GameIdConstraint;
import com.example.cardgame.validators.UserIdConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@Transactional
public class GameProcessService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    ObjectMapper mapper;

    public User CalculateInitialUserCards(@UserIdConstraint String userId, @GameIdConstraint String gameId){
        var user = userRepository.findById(userId).get();
        var game = gameRepository.findById(gameId).get();


        Random rand = new Random();
        for (int cardId = 1; cardId <= 27; cardId++){
            var cardsDeck = game.getGameDeck();
            var cardIdToFetch = cardsDeck.size() == 27 ? cardId : rand.nextInt(cardsDeck.size()); // so if the deck is not full already, we will fetch random cards from the deck without random
            var card = cardsDeck.get(cardIdToFetch);
            user.addCard(new Card(card));
            game.RemoveCardFromDeck(card);
        }
        gameRepository.save(game);
        userRepository.save(user);
        return user;
    }

    public List<Card> GetUserOpponnentCards(@UserIdConstraint String userId, @GameIdConstraint String gameId){
        var game = gameRepository.findById(gameId).get();
        var user = userRepository.findById(userId).get();
        var opponent = game.getCurrentPlayers().entrySet().stream().filter(x -> !x.getKey().equals(user.getId())).findFirst().get().getKey();

        var opponentUser = userRepository.findById(opponent).get();
        return opponentUser.getCurrentCards();
    }

    public GameStatusAfterMove MakeUsersMove(@UserIdConstraint String userId, @GameIdConstraint String gameId){
        var game = gameRepository.findById(gameId).get();
        var user = userRepository.findById(userId).get(); // the user that made the move
        var opponentUserId = game.getCurrentPlayers().entrySet().stream().filter(x -> !x.getKey().equals(user.getId())).findFirst().get().getKey();
        var opponentUser = userRepository.findById(opponentUserId).get();

        var statusToReturn = GameStatusAfterMove.PENDING_FOR_ALL_PLAYERS_TO_MOVE;


        var userMove = user.getCurrentMove(); // move made by the user
        var opponentCurrentMove = opponentUser.getCurrentMove(); // move made by the opponent
        if(userMove != null && opponentCurrentMove != null){
            var userCard = userMove.getCard();
            var opponentCard = opponentCurrentMove.getCard();
            var gameStatus = EvaluateMove(userCard, opponentCard);
            if(gameStatus == GameStatusAfterMove.WIN){
                user.setCurrentScore(user.getCurrentScore() + 1);
                if(user.getGameStatusAfterLastMove() != null && user.getGameStatusAfterLastMove() == GameStatusAfterMove.DRAW){
                    user.setCurrentScore(user.getCurrentScore() + 1);
                }
            }
            else if(gameStatus == GameStatusAfterMove.LOOSE){
                opponentUser.setCurrentScore(opponentUser.getCurrentScore() - 1);
                if(user.getGameStatusAfterLastMove() != null && user.getGameStatusAfterLastMove() == GameStatusAfterMove.DRAW){
                    user.setCurrentScore(user.getCurrentScore() - 1);
                }
            }
            user.setCurrentMove(null);
            opponentUser.setCurrentMove(null);
            user.setGameStatusAfterLastMove(gameStatus);
            statusToReturn = gameStatus;
        }
        else if(userMove == null){
            var playedCard = user.removeLastCard();
            user.setCurrentMove(new Move(playedCard));
        }


        return statusToReturn;
    }

    private GameStatusAfterMove EvaluateMove(Card userCard, Card opponentCard){
        var userCardRank = userCard.getRank();
        var opponentCardRank = opponentCard.getRank();
        if(userCardRank > opponentCardRank){
            return GameStatusAfterMove.WIN;
        }
        else if(userCardRank < opponentCardRank){
            return GameStatusAfterMove.LOOSE;
        }
        else{
            return GameStatusAfterMove.DRAW;
        }
    }
}
