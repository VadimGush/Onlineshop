package net.thumbtack.onlineshop.database.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="client")
public class Client {

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy="increment")
    private Long id;

    private String firstName;
    private String secondName;
    private String thirdName;

    private String email;

    private String postAddress;
    private String phone;

    private String login;
    private String password;


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
