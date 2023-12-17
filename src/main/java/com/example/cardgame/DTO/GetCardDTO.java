package com.example.cardgame.DTO;

import lombok.Data;

@Data
public class GetCardDTO {
    private String id;
    private int rank;
    private String representation;
}
