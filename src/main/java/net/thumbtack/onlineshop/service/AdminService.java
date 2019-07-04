package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.database.models.Administrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private AccountDao accountDao;
    private SessionDao sessionDao;

    @Autowired
    public AdminService(AccountDao accountDao, SessionDao sessionDao) {
        this.accountDao = accountDao;
        this.sessionDao = sessionDao;
    }

    public Account register(Account admin) throws ServiceException {
        if (accountDao.exists(admin.getLogin()))
            throw new ServiceException(ServiceException.ErrorCode.LOGIN_ALREADY_IN_USE);

        accountDao.insert(admin);
        return admin;
    }

    public Account edit(String sessionId) {
        return null;
    }

    public List<Account> getAll(String sessionId) {

        // Проверяем администратор ли

        // Если да, то выдаём весь список клиентов

        return accountDao.getAll();

    }

}
