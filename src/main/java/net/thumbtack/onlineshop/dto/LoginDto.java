package net.thumbtack.onlineshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginDto {

    @JsonProperty(required = true)
    private String login;

    @JsonProperty(required = true)
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
