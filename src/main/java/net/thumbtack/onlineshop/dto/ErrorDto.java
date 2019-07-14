package net.thumbtack.onlineshop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDto {

    private List<Error> errors = new ArrayList<>();

    public ErrorDto(String errorCode, String field, String message) {
        errors.add(new Error(errorCode, field, message));
    }

    public ErrorDto() {

    }

    public List<Error> getErrors() {
        return errors;
    }

    public void addError(String errorCode, String field, String message) {
        errors.add(new Error(errorCode, field, message));
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Error {

        private String errorCode;
        private String field;
        private String message;

        public Error(String errorCode, String field, String message) {
            this(errorCode, message);
            this.field = field;
        }

        public Error(String errorCode, String message) {
            this.errorCode = errorCode;
            this.message = message;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
