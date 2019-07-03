package net.thumbtack.onlineshop.database.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="admin")
public class Administrator {

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy="increment")
    private Long id;

    private String firstName;
    private String secondName;
    private String thirdName;

    private String profession;
    private String login;
    private String password;

    public Administrator() {}

    public Administrator(String firstName, String secondName, String thirdName, String profession, String login, String password) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.thirdName = thirdName;
        this.profession = profession;
        this.login = login;
        this.password = password;
    }

    public Administrator(String firstName, String secondName, String profession, String login, String password) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.profession = profession;
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

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
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
