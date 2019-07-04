package net.thumbtack.onlineshop.database.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="client")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;

    private String secondName;

    private String thirdName;

    private String email;

    private String postAddress;

    private String phone;

    @Column(unique = true)
    private String login;

    private String password;

    @Column(columnDefinition = "int default 0")
    private Integer deposit;

    private String profession;

    private Boolean admin = false;

    public Account() {}

    protected Account(
            String firstName,
            String secondName,
            String thirdName,
            String email,
            String postAddress,
            String phone,
            String login,
            String password
    ) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.thirdName = thirdName;
        this.email = email;
        this.postAddress = postAddress;
        this.phone = phone;
        this.login = login;
        this.password = password;
        this.deposit = 0;
    }

    protected Account(
            String firstName,
            String secondName,
            String email,
            String postAddress,
            String phone,
            String login,
            String password
    ) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.postAddress = postAddress;
        this.phone = phone;
        this.login = login;
        this.password = password;
        this.deposit = 0;
    }

    protected Account(
            String firstName,
            String secondName,
            String thirdName,
            String profession,
            String login,
            String password
    ) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.thirdName = thirdName;
        this.profession = profession;
        this.login = login;
        this.password = password;
        this.admin = true;
    }

    protected Account(
            String firstName,
            String secondName,
            String profession,
            String login,
            String password
    ) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.profession = profession;
        this.login = login;
        this.password = password;
        this.admin = true;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
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

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getThirdName() {
        return thirdName;
    }

    public void setThirdName(String thirdName) {
        this.thirdName = thirdName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPostAddress() {
        return postAddress;
    }

    public void setPostAddress(String postAddress) {
        this.postAddress = postAddress;
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
                Objects.equals(secondName, account.secondName) &&
                Objects.equals(thirdName, account.thirdName) &&
                Objects.equals(email, account.email) &&
                Objects.equals(postAddress, account.postAddress) &&
                Objects.equals(phone, account.phone) &&
                Objects.equals(login, account.login) &&
                Objects.equals(password, account.password) &&
                Objects.equals(deposit, account.deposit) &&
                Objects.equals(profession, account.profession) &&
                Objects.equals(admin, account.admin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, secondName, thirdName, email, postAddress, phone, login, password, deposit, profession, admin);
    }
}
