package com.example.cardgame.DTO;

import lombok.Data;

import java.util.List;

@Data
public class GetUserDTO {
    private String userId;
    private String name;
    private int currentVictories;
    private List<GetCardDTO> currentCards;
}
