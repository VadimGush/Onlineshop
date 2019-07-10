package net.thumbtack.onlineshop.controller.validation;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static net.thumbtack.onlineshop.controller.validation.ValidatorUtils.setMessage;

public class PasswordValidator implements
        ConstraintValidator<Password, String> {

    @Value("${min_password_length}")
    private int minPasswordLength;

    @Override
    public void initialize(Password constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.isEmpty()) {
            setMessage(context, "Пароль не заполнен");
            return false;
        }

        if (value.length() < minPasswordLength) {
            setMessage(context, "Пароль должен содержать минимум " + minPasswordLength + " символов");
            return false;
        }

        return true;

    }
}
