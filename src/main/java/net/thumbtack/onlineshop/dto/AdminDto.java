package net.thumbtack.onlineshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.thumbtack.onlineshop.request.LoginRequest;

public class AdminDto extends LoginRequest {

    private long id;
    private String firstName;
    private String lastName;

    @JsonProperty(required = true)
    private String patronymic;

    private String position;

    public AdminDto(String firstName, String lastName, String patronymic, String position, String login, String password) {
        super(login, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.position = position;
    }

    public AdminDto(String firstName, String lastName, String position, String login, String password) {
        super(login, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
    }

    public AdminDto(long id, String firstName, String lastName, String patronymic, String position) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.position = position;
    }

    public AdminDto(long id, String firstName, String lastName, String position) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
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
