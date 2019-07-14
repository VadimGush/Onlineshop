package net.thumbtack.onlineshop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import net.thumbtack.onlineshop.controller.validation.OptionalName;
import net.thumbtack.onlineshop.controller.validation.Password;
import net.thumbtack.onlineshop.controller.validation.Phone;
import net.thumbtack.onlineshop.controller.validation.RequiredName;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientEditDto {

    @RequiredName
    private String firstName;
    @RequiredName
    private String lastName;
    @OptionalName
    private String patronymic;
    @Email
    private String email;
    @NotBlank
    private String address;
    @Phone
    private String phone;
    @Password
    private String oldPassword;
    @Password
    private String newPassword;

    public ClientEditDto() {

    }

    public ClientEditDto(String firstName, String lastName, String patronymic, String email, String address, String phone, String oldPassword, String newPassword) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.email = email;
        this.address = address;
        this.phone = phone;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
