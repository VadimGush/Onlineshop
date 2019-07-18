package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.database.models.Session;

public class GeneralService {

    private SessionDao sessionDao;

    public GeneralService(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    Account getAdmin(String sessionId) throws ServiceException {
        Account account = getAccount(sessionId);

        if (!account.isAdmin()) {
            throw new ServiceException(ServiceException.ErrorCode.NOT_ADMIN);
        }

        return account;
    }

    Account getClient(String sessionId) throws ServiceException {
        Account account = getAccount(sessionId);

        if (account.isAdmin()) {
            throw new ServiceException(ServiceException.ErrorCode.NOT_CLIENT);
        }

        return account;
    }

    public Account getAccount(String sessionId) throws ServiceException {
        Session session = sessionDao.get(sessionId);

        if (session == null) {
            throw new ServiceException(ServiceException.ErrorCode.NOT_LOGIN);
        }

        return session.getAccount();
    }
}
