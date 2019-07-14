package net.thumbtack.onlineshop.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import net.thumbtack.onlineshop.controller.validation.OptionalRussianName;
import net.thumbtack.onlineshop.controller.validation.Password;
import net.thumbtack.onlineshop.controller.validation.RequiredName;
import net.thumbtack.onlineshop.controller.validation.RequiredRussianName;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminEditDto {

    @RequiredRussianName
    private String firstName;
    @RequiredRussianName
    private String lastName;
    @OptionalRussianName
    private String patronymic;
    @RequiredName
    private String position;
    @NotBlank
    private String oldPassword;
    @Password
    private String newPassword;

    public AdminEditDto() {

    }

    public AdminEditDto(String firstName, String lastName, String patronymic, String position, String oldPassword, String newPassword) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.position = position;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
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

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
