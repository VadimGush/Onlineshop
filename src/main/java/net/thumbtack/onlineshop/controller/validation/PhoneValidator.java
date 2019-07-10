package net.thumbtack.onlineshop.controller.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static net.thumbtack.onlineshop.controller.validation.ValidatorUtils.setMessage;

public class PhoneValidator implements
        ConstraintValidator<Phone, String> {

    @Override
    public void initialize(Phone constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.isEmpty()) {
            setMessage(context, "Не указан номер телефона");
            return false;
        }

        setMessage(context, "Неверный формат телефона");

        if (value.startsWith("+7") || value.startsWith("8")) {

            value = value.replaceAll("\\+", "");
            value = value.replaceAll("-", "");

            if (value.length() != 11 || !value.chars().allMatch(Character::isDigit))
                return false;

        } else {
            return false;
        }

        return true;

    }
}
