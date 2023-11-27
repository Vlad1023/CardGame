package com.example.cardgame.DTO;

import lombok.Data;

@Data
public class GetGameDTO {
    private String gameId;
    private String gameName;
    private Integer usersCount;
    private Boolean isGameStarted;
}
