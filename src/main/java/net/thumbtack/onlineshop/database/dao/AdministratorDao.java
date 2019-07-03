package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.database.DatabaseSessionService;
import net.thumbtack.onlineshop.database.models.Administrator;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdministratorDao {

    private DatabaseSessionService service;

    @Autowired
    public AdministratorDao(DatabaseSessionService service) {
        this.service = service;
    }

    public Administrator insert(Administrator admin) {
        Session session = service.getSession();
        session.beginTransaction();
        session.save(admin);
        session.getTransaction().commit();
        session.close();
        return admin;
    }



}
