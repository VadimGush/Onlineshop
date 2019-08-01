package net.thumbtack.onlineshop.dto.validation;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static net.thumbtack.onlineshop.dto.validation.ValidatorUtils.setMessage;

public class RequiredPasswordValidator implements
        ConstraintValidator<RequiredPassword, String> {

    @Value("${min_password_length}")
    private int minPasswordLength;

    @Override
    public void initialize(RequiredPassword constraintAnnotation) {
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
