package net.thumbtack.onlineshop.service;

public class ServiceException extends Exception {

    public enum ErrorCode {
        LOGIN_ALREADY_IN_USE("LOGIN_ALREADY_IN_USE", "Данный логин занят");

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

    public ServiceException(ErrorCode code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return code.getMessage();
    }


    public ErrorCode getErrorCode() {
        return code;
    }

}
