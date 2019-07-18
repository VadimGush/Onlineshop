package net.thumbtack.onlineshop.dto.validation;

import org.springframework.validation.Errors;

/**
 * Исключение содержащие в себе список всех ошибок валидации
 */
public class ValidationException extends Exception {

    private Errors errors;

    public ValidationException(Errors errors) {
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }
}

