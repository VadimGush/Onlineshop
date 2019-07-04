package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.database.models.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SessionService {

    private SessionDao sessionDao;
    private AccountDao accountDao;

    @Autowired
    public SessionService(AccountDao accountDao, SessionDao sessionDao) {
        this.sessionDao = sessionDao;
        this.accountDao = accountDao;
    }

    public String login(String login, String password) throws ServiceException {
        Account account = accountDao.get(login, password);
        if (account == null)
            throw new ServiceException(ServiceException.ErrorCode.USER_NOT_FOUND);

        Session session = new Session(UUID.randomUUID().toString(), account);
        sessionDao.insert(session);
        return session.getUUID();
    }

    public void logout(String sessionId) {
        sessionDao.delete(sessionDao.get(sessionId));
    }

}
