package com.example.cardgame.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
public class User {
    @Id
    private String id;

    private String userName;

    public User(String userName) {
        this.userName = userName;
    }
}
