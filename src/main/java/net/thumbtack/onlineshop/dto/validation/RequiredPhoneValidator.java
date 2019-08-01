package net.thumbtack.onlineshop.dto.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static net.thumbtack.onlineshop.dto.validation.ValidatorUtils.setMessage;

public class RequiredPhoneValidator implements
        ConstraintValidator<RequiredPhone, String> {

    @Override
    public void initialize(RequiredPhone constraintAnnotation) {

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

            return value.length() == 11 && value.chars().allMatch(Character::isDigit);

        } else {
            return false;
        }

    }
}
