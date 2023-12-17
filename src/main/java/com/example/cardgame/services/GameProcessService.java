package com.example.cardgame.services;

import com.example.cardgame.models.Card;
import com.example.cardgame.models.Game;
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
}
