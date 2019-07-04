package net.thumbtack.onlineshop.database.models;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String UUID;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Account account;

    public Session() {

    }

    public Session(String UUID, Account account) {
        this.UUID = UUID;
        this.account = account;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public boolean getAccount() {
        return account != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getClient() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

}
