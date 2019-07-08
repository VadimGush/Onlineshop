package net.thumbtack.onlineshop.database.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="client")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String patronymic;
    private String email;
    private String address;
    private String phone;

    @Column(unique = true)
    private String login;

    private String password;

    @Column(columnDefinition = "int default 0")
    private Integer deposit;

    private String position;

    private Boolean admin = false;

    public Account() {}

    // Конструктор для клиента
    protected Account(
            String firstName,
            String lastName,
            String patronymic,
            String email,
            String address,
            String phone,
            String login,
            String password
    ) {
        this(firstName, lastName, email, address, phone, login, password);
        this.patronymic = patronymic;
    }

    // Конструктор для клиента
    protected Account(
            String firstName,
            String lastName,
            String email,
            String address,
            String phone,
            String login,
            String password
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.login = login;
        this.password = password;
        this.deposit = 0;
    }

    // Конструктор для администратора
    protected Account(
            String firstName,
            String lastName,
            String patronymic,
            String position,
            String login,
            String password
    ) {
        this(firstName, lastName, position, login, password);
        this.patronymic = patronymic;
    }

    // Конструктор для администратора
    protected Account(
            String firstName,
            String lastName,
            String position,
            String login,
            String password
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.login = login;
        this.password = password;
        this.admin = true;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Boolean isAdmin() {
        return admin;
    }

    public Integer getDeposit() {
        return deposit;
    }

    public void setDeposit(Integer deposit) {
        this.deposit = deposit;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) &&
                Objects.equals(firstName, account.firstName) &&
                Objects.equals(lastName, account.lastName) &&
                Objects.equals(patronymic, account.patronymic) &&
                Objects.equals(email, account.email) &&
                Objects.equals(address, account.address) &&
                Objects.equals(phone, account.phone) &&
                Objects.equals(login, account.login) &&
                Objects.equals(password, account.password) &&
                Objects.equals(deposit, account.deposit) &&
                Objects.equals(position, account.position) &&
                Objects.equals(admin, account.admin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, patronymic, email, address, phone, login, password, deposit, position, admin);
    }
}
