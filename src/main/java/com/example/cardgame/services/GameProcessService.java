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
import com.fasterxml.jackson.core.JsonProcessingException;
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
    UserService userService;
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
        var halfOfDeck = game.getInitialSizeOfDeck() / 2;
        for (int cardId = 0; cardId < halfOfDeck; cardId++){
            var cardsDeck = game.getGameDeck();
            var cardIdToFetch = cardsDeck.size() == halfOfDeck ? cardId : rand.nextInt(cardsDeck.size()); // so if the deck is not full already, we will fetch without random
            var card = cardsDeck.get(cardIdToFetch);
            user.addCard(new Card(card));
            game.RemoveCardFromDeck(card);
        }
        gameRepository.save(game);
        userRepository.save(user);
        return user;
    }


    public GameStatusAfterMove MakeUsersMove(@UserIdConstraint String userId, @GameIdConstraint String gameId) throws JsonProcessingException {
        var game = gameRepository.findById(gameId).get();
        var user = userRepository.findById(userId).get(); // the user that made the move
        var opponentUserId = userService.GetUserOpponentId(game, user);
        var opponentUser = userRepository.findById(opponentUserId).get();

        var statusToReturn = GameStatusAfterMove.PENDING_FOR_ALL_PLAYERS_TO_MOVE;


        var userMove = user.getCurrentMove(); // move made by the user
        var opponentCurrentMove = opponentUser.getCurrentMove(); // move made by the opponent
        if(userMove == null){
            var userCard = user.removeFirstCard();
            user.setCurrentMove(new Move(userCard));

            if(opponentCurrentMove != null){
                var opponentCard = opponentCurrentMove.getCard();
                var gameStatus = EvaluateMove(userCard, opponentCard);
                if(gameStatus == GameStatusAfterMove.WIN){
                    user.addVictory();
                }
                else if(gameStatus == GameStatusAfterMove.LOOSE){
                    opponentUser.addVictory();
                }
                user.setCurrentMove(null);
                opponentUser.setCurrentMove(null);
                statusToReturn = gameStatus;
            }
            this.notifyUserThatOpponentMadeAMove(opponentUserId, statusToReturn);
        }
        if(user.getCurrentCards().size() == 0 && opponentUser.getCurrentCards().size() == 0){
            this.FinishGame(game);
        }
        userRepository.save(user);
        userRepository.save(opponentUser);

        return statusToReturn;
    }

    private void FinishGame(Game game){
        gameRepository.delete(game);
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

    private void notifyUserThatOpponentMadeAMove(String opponentUserId, GameStatusAfterMove gameStatusAfterMove) throws JsonProcessingException {
        var destination = "/game/opponentMadeMove";
        messagingTemplate.convertAndSendToUser(opponentUserId, destination, gameStatusAfterMove);
    }
}
