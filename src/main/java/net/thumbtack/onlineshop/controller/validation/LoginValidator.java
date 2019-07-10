package net.thumbtack.onlineshop.controller.validation;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static net.thumbtack.onlineshop.controller.validation.ValidatorUtils.setMessage;

public class LoginValidator implements
        ConstraintValidator<Login, String> {

    @Value("${max_name_length}")
    private int maxNameLength;

    @Override
    public void initialize(Login constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.isEmpty()) {
            setMessage(context, "Логин не заполнен");
            return false;
        }

        if (value.length() > maxNameLength) {
            setMessage(context, "Превышена максимальная длина логина");
            return false;
        }

        if (!value.chars().allMatch((c) -> Character.isLetter(c) || Character.isDigit(c))) {
            setMessage(context, "Логин должен содержать только буквы и цифры");
            return false;
        }

        return true;
    }
}
