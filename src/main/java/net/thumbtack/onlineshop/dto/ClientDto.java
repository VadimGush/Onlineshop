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

    public ClientDto() {

    }

    public ClientDto(Account account) {
        super(account.getLogin(), account.getPassword());
        this.id = account.getId() == null ? 0 : account.getId();
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
        this.patronymic = account.getPatronymic();
        this.email = account.getEmail();
        this.address = account.getAddress();
        this.phone = account.getPhone();
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
