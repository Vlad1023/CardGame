package com.example.cardgame.models;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("User")
public class User {
    @Id
    @Getter
    private String id;
    @Getter
    private String name;

    public User(String name) {
        this.name = name;
    }
}
