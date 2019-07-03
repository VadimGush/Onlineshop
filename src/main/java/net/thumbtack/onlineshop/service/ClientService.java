package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.ClientDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Client;
import net.thumbtack.onlineshop.database.models.Product;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private ClientDao clientDao;
    private SessionDao sessionDao;

    public ClientService(ClientDao clientDao, SessionDao sessionDao) {
        this.clientDao = clientDao;
        this.sessionDao = sessionDao;
    }

    public Client register(Client client) {
        return null;
    }

    public Client getAccount(String sessionId) {
        return null;
    }

    public Client edit(String sessionId, Client client) {
        return null;
    }

    public void putDeposit(String sessionId, int amount) {

    }

    public int getDeposit(String sessionId) {
        return 0;
    }

    public void buyProduct(String sessionId, Product product) {

    }

    public void addToBasket(String sessionId, Product product) {

    }

    public void deleteFromBasket(String sessionId, Product product) {

    }

    public void editProductCount(String sessionId, Product product) {

    }

    public void getBasket(String sessionId) {

    }

    public void buyBasket(String sessionId) {

    }

}
