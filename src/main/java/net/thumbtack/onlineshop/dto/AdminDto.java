package net.thumbtack.onlineshop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import net.thumbtack.onlineshop.controller.validation.OptionalName;
import net.thumbtack.onlineshop.controller.validation.RequiredName;
import net.thumbtack.onlineshop.database.models.Account;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminDto extends LoginDto {

    private long id;

    @RequiredName
    private String firstName;
    @RequiredName
    private String lastName;
    @OptionalName
    private String patronymic;
    @RequiredName
    private String position;

    public AdminDto() {

    }

    // Основной конструктор с обязательным полями
    public AdminDto(String firstName, String lastName, String position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
    }

    // Регистрация администратора без отчества
    public AdminDto(String firstName, String lastName, String position, String login, String password) {
        this(firstName, lastName, position);
        this.setLogin(login);
        this.setPassword(password);
    }

    // Регистрация администратора с отчеством
    public AdminDto(String firstName, String lastName, String patronymic, String position, String login, String password) {
        this(firstName, lastName, position, login, password);
        this.patronymic = patronymic;
    }

    // Создание объекта из энтити БД
    public AdminDto(Account account) {
        super(account.getLogin(), account.getPassword());
        this.id = account.getId() == null ? 0 : account.getId();
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
        this.patronymic = account.getPatronymic();
        this.position = account.getPosition();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
