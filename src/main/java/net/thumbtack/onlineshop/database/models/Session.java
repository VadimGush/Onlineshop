package net.thumbtack.onlineshop.database.models;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Objects;

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

    public Account getAccount() {
        return account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Session)) return false;
        Session session = (Session) o;
        return Objects.equals(id, session.id) &&
                Objects.equals(UUID, session.UUID) &&
                Objects.equals(account, session.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, UUID, account);
    }
}
