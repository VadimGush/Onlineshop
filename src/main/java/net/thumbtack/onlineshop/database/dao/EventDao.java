package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.database.DatabaseSessionService;
import net.thumbtack.onlineshop.database.models.Event;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventDao {

    private DatabaseSessionService service;

    @Autowired
    public EventDao(DatabaseSessionService service) {
        this.service = service;
    }

    public Event insertEvent(Event event) {

        Session session = service.getSession();
        session.beginTransaction();
        session.save(event);
        session.getTransaction().commit();
        session.close();

        return event;
    }

}
