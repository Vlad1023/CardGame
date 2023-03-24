package com.example.cardgame.DTO;

import com.example.cardgame.models.User;
import lombok.Data;

@Data
public class NewGameDTO {
    private String gameName;
    private String userId;
}
