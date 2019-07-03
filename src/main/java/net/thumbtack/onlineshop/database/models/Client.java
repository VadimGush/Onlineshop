package net.thumbtack.onlineshop.database.models;

import javax.persistence.*;

@Entity
@Table(name="client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String secondName;

    private String thirdName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String postAddress;

    @Column(nullable = false)
    private String phone;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer deposit;


    public Client() {}

    public Client(
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
    }

    public Client(
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
}
