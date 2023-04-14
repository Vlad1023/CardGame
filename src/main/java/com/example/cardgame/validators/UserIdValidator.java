package com.example.cardgame.validators;

import com.example.cardgame.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UserIdValidator implements
        ConstraintValidator<UserIdConstraint, String> {

    @Autowired
    UserRepository userRepository;

    @Override
    public void initialize(UserIdConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String userId, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.buildConstraintViolationWithTemplate("Invalid user id")
                .addConstraintViolation();
        if(userRepository.existsById(userId))
            return true;
        return false;
    }
}
