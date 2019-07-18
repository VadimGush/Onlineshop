package net.thumbtack.onlineshop.dto.validation;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static net.thumbtack.onlineshop.dto.validation.ValidatorUtils.setMessage;

public class RequiredNameValidator implements
        ConstraintValidator<RequiredName, String> {

    @Value("${max_name_length}")
    private int maxNameLength;

    @Override
    public void initialize(RequiredName constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.isEmpty()) {
            setMessage(context, "Поле не заполнено");
            return false;
        }

        if (value.length() > maxNameLength) {
            setMessage(context, "Превышена максимальная длина поля");
            return false;
        }

        return true;
    }

}
