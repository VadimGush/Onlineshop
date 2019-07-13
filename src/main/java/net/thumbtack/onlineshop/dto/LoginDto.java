package net.thumbtack.onlineshop.dto;

import net.thumbtack.onlineshop.controller.validation.Login;
import net.thumbtack.onlineshop.controller.validation.Password;

public class LoginDto {

    @Login
    private String login;

    @Password
    private String password;

    public LoginDto() {

    }

    public LoginDto(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
