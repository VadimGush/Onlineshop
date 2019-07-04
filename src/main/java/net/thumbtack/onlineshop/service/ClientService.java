package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.database.models.Product;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private AccountDao clientDao;
    private SessionDao sessionDao;

    public ClientService(AccountDao clientDao, SessionDao sessionDao) {
        this.clientDao = clientDao;
        this.sessionDao = sessionDao;
    }

    public Account register(Account account) {
        return null;
    }

    public Account getAccount(String sessionId) {
        return null;
    }

    public Account edit(String sessionId, Account account) {
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
