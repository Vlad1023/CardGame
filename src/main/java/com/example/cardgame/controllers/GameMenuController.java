package com.example.cardgame.controllers;

import com.example.cardgame.DTO.GetGameDTO;
import com.example.cardgame.DTO.NewGameDTO;
import com.example.cardgame.models.Game;
import com.example.cardgame.repositories.GameRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Controller
@RestController
public class GameMenuController {
    @Autowired
    GameRepository gameRepository;
    @Autowired
    SimpMessagingTemplate messagingTemplate;
    @Autowired
    ObjectMapper mapper;

    @Autowired
    private ModelMapper modelMapper;

    @MessageMapping("/addGame")
    @SendTo("/gamesInfo/gamesList")
    public String addGameToList(NewGameDTO DTO) throws JsonProcessingException {
        Game newGame = new Game(DTO.getGameName());
        newGame.AddPlayer(DTO.getUserNameHost());
        gameRepository.save(newGame);
        List<Game> games = (List<Game>) gameRepository.findAll();
        List<GetGameDTO> getGamesDTO = new ArrayList<GetGameDTO>();
        for (Game game : games) {
            getGamesDTO.add(modelMapper.map(game, GetGameDTO.class));
        }
        return mapper.writeValueAsString(getGamesDTO);
    }


    @GetMapping(value = "/activePendingGames")
    @ResponseBody
    public ResponseEntity<List<GetGameDTO>> GetAllActivePendingGames(){
        List<Game> games = (List<Game>) gameRepository.findAll();
        List<GetGameDTO> getGamesDTO = new ArrayList<GetGameDTO>();
        for (Game game : games) {
            getGamesDTO.add(modelMapper.map(game, GetGameDTO.class));
        }
        return new ResponseEntity<List<GetGameDTO>>(getGamesDTO, HttpStatus.OK);
    }
}
