package net.thumbtack.onlineshop.database.models;

import javax.persistence.*;

@Deprecated
@Entity
@Table(name="admin")
public class Administrator {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String secondName;

    private String thirdName;

    @Column(nullable = false)
    private String profession;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
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
