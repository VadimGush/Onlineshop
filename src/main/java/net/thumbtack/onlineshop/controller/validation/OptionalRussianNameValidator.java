package net.thumbtack.onlineshop.controller.validation;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static net.thumbtack.onlineshop.controller.validation.ValidatorUtils.setMessage;

public class OptionalRussianNameValidator implements
        ConstraintValidator<OptionalRussianName, String> {

    private static String russian = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя -";

    @Value("${max_name_length}")
    private int maxNameLength;

    @Override
    public void initialize(OptionalRussianName constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value != null) {
            if (value.length() > maxNameLength) {
                setMessage(context, "Превышена максимальная длина поля");
                return false;
            }

            if (!value.toLowerCase().chars()
                    .allMatch(
                            c -> russian.contains(String.valueOf((char)c))
                    )) {
                setMessage(context, "Имя должно содержать только буквы русского алфавита");
                return false;
            }
        }


        return true;
    }

}
