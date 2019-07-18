package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.dto.ErrorDto;
import net.thumbtack.onlineshop.dto.validation.ValidationException;
import net.thumbtack.onlineshop.service.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public ErrorDto handleServiceException(ServiceException exception) {
        return new ErrorDto(
                exception.getErrorCode().getErrorCode(),
                exception.getField(),
                exception.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public ErrorDto handleValidationException(ValidationException exception) {
        ErrorDto errors = new ErrorDto();

        for (FieldError e : exception.getErrors().getFieldErrors()) {
            errors.addError(e.getCode(), e.getField(), e.getDefaultMessage());
        }

        return errors;
    }


}
