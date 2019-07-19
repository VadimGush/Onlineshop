package net.thumbtack.onlineshop.service;

public class ServiceException extends Exception {

    public enum ErrorCode {
        LOGIN_ALREADY_IN_USE    ("LoginInUse", "Данный логин занят"),
        USER_NOT_FOUND          ("UserNotFound", "Пользователь с таким логином и паролем не найден"),
        NOT_ADMIN               ("NotAdmin", "Для вызова данного метода необходимо иметь права администратора"),
        WRONG_PASSWORD          ("WrongPassword", "Неверный пароль"),
        NOT_LOGIN               ("NotLogin", "Необходимо войти в систему"),
        NOT_CLIENT              ("NotClient", "Для вызова данного метода необходимо быть клиентом"),
        PRODUCT_NOT_FOUND       ("ProductNotFound", "Указанный товар не найден"),
        WRONG_PRODUCT_INFO      ("WrongProductInfo", "Неверно указана информация о товаре"),
        NOT_ENOUGH_PRODUCT      ("NotEnoughProduct", "Недостаточно товара"),
        NOT_ENOUGH_MONEY        ("NotEnoughMoney", "Недостаточно денег на счету"),
        CATEGORY_NOT_FOUND      ("CategoryNotFound", "Категория не найдена"),
        SAME_CATEGORY_NAME      ("SameCategoryName", "Категория с таким именем уже существует"),
        SECOND_SUBCATEGORY      ("SecondSubcategory", "Родитель категории не может быть подкатегорией"),
        CATEGORY_TO_SUBCATEGORY ("CategoryToSubcategory", "Нельзя изменить категорию на подкатегорию"),
        EDIT_CATEGORY_EMPTY     ("EditCategoryEmpty", "Хотя бы одно поле должно быть заполнено"),
        REQUIRED_COUNT          ("RequiredCount", "Не указано количество товара");

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
