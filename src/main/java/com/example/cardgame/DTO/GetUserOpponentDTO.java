package com.example.cardgame.DTO;

import lombok.Data;

import java.util.List;

@Data
public class GetUserOpponentDTO {
    private String name;
    private List<GetCardDTO> opponentCardsDTO;

    public GetUserOpponentDTO(String name, List<GetCardDTO> opponentCardsDTO) {
        this.name = name;
        this.opponentCardsDTO = opponentCardsDTO;
    }
}
