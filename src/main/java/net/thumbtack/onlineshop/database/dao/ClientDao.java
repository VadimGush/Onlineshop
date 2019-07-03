package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.database.DatabaseSessionService;
import net.thumbtack.onlineshop.database.models.Client;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientDao {

    private DatabaseSessionService service;

    @Autowired
    public ClientDao(DatabaseSessionService service) {
        this.service = service;
    }

    public Client insert(Client client) {
        Session session = service.getSession();
        session.beginTransaction();
        session.save(client);
        session.getTransaction().commit();
        session.close();
        return client;
    }
}
