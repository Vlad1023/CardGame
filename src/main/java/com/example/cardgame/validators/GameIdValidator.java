package com.example.cardgame.validators;

import com.example.cardgame.repositories.GameRepository;
import com.example.cardgame.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class GameIdValidator implements ConstraintValidator<GameIdConstraint, String> {
    @Autowired
    GameRepository gameRepository;
    @Override
    public void initialize(GameIdConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String gameId, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.buildConstraintViolationWithTemplate("Invalid game id")
                .addConstraintViolation();
        if(gameRepository.existsById(gameId))
            return true;
        return false;
    }
}
