package net.thumbtack.onlineshop.dto;

import net.thumbtack.onlineshop.database.models.Account;

public class ClientDto extends LoginDto {

    private long id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String email;
    private String address;
    private String phone;

    public ClientDto(long id, String firstName, String lastName, String patronymic, String email, String address, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.email = email;
        this.address = address;
        this.phone = phone;
    }

    public ClientDto(String login, String password, long id, String firstName, String lastName, String patronymic, String email, String address, String phone) {
        super(login, password);
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.email = email;
        this.address = address;
        this.phone = phone;
    }

    public ClientDto(Account account) {
        super(account.getLogin(), account.getPassword());
        this.id = account.getId();
        this.firstName = account.getFirstName();
        this.lastName = account.getSecondName();
        this.patronymic = account.getThirdName();
        this.email = account.getEmail();
        this.address = account.getPostAddress();
        this.phone = account.getPhone();
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

}
