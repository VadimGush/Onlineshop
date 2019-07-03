package net.thumbtack.onlineshop.database.models;

import org.hibernate.annotations.Cascade;
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
    private Client client;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Administrator admin;

    public Session() {

    }

    public Session(String UUID, Client client) {
        this.UUID = UUID;
        this.client = client;
    }

    public Session(String UUID, Administrator admin) {
        this.UUID = UUID;
        this.admin = admin;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public boolean isAdmin() {
        return admin != null;
    }

    public boolean isClient() {
        return client != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Administrator getAdmin() {
        return admin;
    }

    public void setAdmin(Administrator admin) {
        this.admin = admin;
    }
}
