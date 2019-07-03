package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.AdministratorDao;
import net.thumbtack.onlineshop.database.dao.ClientDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Administrator;
import net.thumbtack.onlineshop.database.models.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private AdministratorDao adminDao;
    private ClientDao clientDao;
    private SessionDao sessionDao;

    @Autowired
    public AdminService(AdministratorDao adminDao, ClientDao clientDao, SessionDao sessionDao) {
        this.adminDao = adminDao;
        this.clientDao = clientDao;
        this.sessionDao = sessionDao;
    }

    public String register(Administrator admin) {
        return null;
    }

    public Administrator edit(String sessionId) {
        return null;
    }

    public List<Client> getAll(String sessionId) {

        // Проверяем администратор ли

        // Если да, то выдаём весь список клиентов

        return clientDao.getAll();

    }

}
