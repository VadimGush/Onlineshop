package net.thumbtack.onlineshop.controller.validation;

import javax.validation.ConstraintValidatorContext;

public class ValidatorUtils {

    public static void setMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();

        context
                .buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }

}
