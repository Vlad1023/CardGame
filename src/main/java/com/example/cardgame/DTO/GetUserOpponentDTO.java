package com.example.cardgame.DTO;

import lombok.Data;

import java.util.List;

@Data
public class GetUserOpponentDTO {
    private String name;
    private int currentVictories;
    private List<GetCardDTO> opponentCardsDTO;

    public GetUserOpponentDTO(String name, int currentVictories, List<GetCardDTO> opponentCardsDTO) {
        this.name = name;
        this.currentVictories = currentVictories;
        this.opponentCardsDTO = opponentCardsDTO;
    }
}
