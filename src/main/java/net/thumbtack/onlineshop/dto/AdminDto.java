package net.thumbtack.onlineshop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import net.thumbtack.onlineshop.domain.models.Account;
import net.thumbtack.onlineshop.dto.actions.Edit;
import net.thumbtack.onlineshop.dto.actions.Register;
import net.thumbtack.onlineshop.dto.validation.OptionalRussianName;
import net.thumbtack.onlineshop.dto.validation.RequiredName;
import net.thumbtack.onlineshop.dto.validation.RequiredPassword;
import net.thumbtack.onlineshop.dto.validation.RequiredRussianName;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminDto extends LoginDto {

    private Long id;

    @RequiredRussianName(groups = { Register.class, Edit.class })
    private String firstName;

    @RequiredRussianName(groups = { Register.class, Edit.class })
    private String lastName;

    @OptionalRussianName(groups = { Register.class, Edit.class })
    private String patronymic;

    @RequiredName(groups = { Register.class, Edit.class })
    private String position;

    @NotBlank(groups = Edit.class)
    private String oldPassword;

    @RequiredPassword(groups = Edit.class)
    private String newPassword;

    public AdminDto() {

    }

    @Deprecated
    public AdminDto(String firstName, String lastName, String position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
    }

    @Deprecated
    public AdminDto(String firstName, String lastName, String position, String login, String password) {
        this(firstName, lastName, position);
        this.setLogin(login);
        this.setPassword(password);
    }

    @Deprecated
    public AdminDto(String firstName, String lastName, String patronymic, String position, String login, String password) {
        this(firstName, lastName, position, login, password);
        this.patronymic = patronymic;
    }

    public AdminDto(Account account) {
        super(account.getLogin(), account.getPassword());
        this.id = account.getId();
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
        this.patronymic = account.getPatronymic();
        this.position = account.getPosition();
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
