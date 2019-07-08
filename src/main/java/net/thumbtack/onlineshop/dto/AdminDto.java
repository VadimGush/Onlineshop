package net.thumbtack.onlineshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.thumbtack.onlineshop.database.models.Account;

public class AdminDto extends LoginDto {

    private long id;
    private String firstName;
    private String lastName;

    @JsonProperty(required = true)
    private String patronymic;

    private String position;

    public AdminDto(String firstName, String lastName, String patronymic, String position, String login, String password) {
        this(firstName, lastName, position, login, password);
        this.patronymic = patronymic;
    }

    public AdminDto(String firstName, String lastName, String position, String login, String password) {
        super(login, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
    }

    public AdminDto(long id, String firstName, String lastName, String patronymic, String position) {
        this(id, firstName, lastName, position);
        this.patronymic = patronymic;
    }

    public AdminDto(long id, String firstName, String lastName, String position) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
    }

    public AdminDto(Account account) {
        super(account.getLogin(), account.getPassword());
        this.id = account.getId() == null ? 0 : account.getId();
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
        this.patronymic = account.getPatronymic();
        this.position = account.getPosition();
    }

    public long id() {
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
