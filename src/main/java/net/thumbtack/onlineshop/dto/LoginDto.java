package net.thumbtack.onlineshop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import net.thumbtack.onlineshop.dto.actions.Register;
import net.thumbtack.onlineshop.dto.validation.RequiredLogin;
import net.thumbtack.onlineshop.dto.validation.RequiredPassword;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginDto {

    @RequiredLogin(groups = Register.class)
    private String login;

    @RequiredPassword(groups = Register.class)
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
