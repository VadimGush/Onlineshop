package net.thumbtack.onlineshop.controller.validation;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static net.thumbtack.onlineshop.controller.validation.ValidatorUtils.setMessage;

public class OptionalNameValidator implements ConstraintValidator<OptionalName, String> {

    @Value("${max_name_length}")
    private int maxNameLength;

    @Override
    public void initialize(OptionalName constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value != null) {
            if (value.length() > maxNameLength) {
                setMessage(context, "Превышена максимальная длина поля");
                return false;
            }
        }

        return true;
    }
}
