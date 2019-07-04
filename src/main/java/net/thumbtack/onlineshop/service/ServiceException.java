package net.thumbtack.onlineshop.service;

public class ServiceException extends Exception {

    public enum ErrorCode {
        LOGIN_ALREADY_IN_USE("LOGIN_ALREADY_IN_USE", "Данный логин занят"),
        USER_NOT_FOUND("USER_NOT_FOUND", "Пользователь с таким логином и паролем не найден"),
        NOT_ADMIN("NOT_ADMIN", "Для вызова данного метода необходимо иметь права администратора"),
        WRONG_PASSWORD("WRONG_PASSWORD", "Неверный пароль"),
        NOT_LOGIN("NOT_LOGIN", "Необходимо войти в систему"),
        NOT_CLIENT("NOT_CLIENT", "Для вызова данного метода необходимо быть клиентом");

        private String errorCode;
        private String message;

        ErrorCode(String errorCode, String message) {
            this.errorCode = errorCode;
            this.message = message;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getMessage() {
            return message;
        }
    }

    private ErrorCode code;
    private String field;

    public ServiceException(ErrorCode code) {
        this.code = code;
    }

    public ServiceException(ErrorCode code, String field) {
        this.code = code;
        this.field = field;
    }

    @Override
    public String getMessage() {
        return code.getMessage();
    }

    public ErrorCode getErrorCode() {
        return code;
    }

    public String getField() {
        return field;
    }

}