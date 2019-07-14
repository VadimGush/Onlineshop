package net.thumbtack.onlineshop.controller.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OptionalNotBlankValidator implements
        ConstraintValidator<OptionalNotBlank, String> {


    @Override
    public void initialize(OptionalNotBlank constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        return value == null || !value.isEmpty();

    }
}
