package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.domain.dao.SessionDao;
import net.thumbtack.onlineshop.domain.models.Account;
import net.thumbtack.onlineshop.domain.models.Session;

/**
 * Абстрактный сервис с набором методов идентификации пользователей
 */
public abstract class GeneralService {

    private SessionDao sessionDao;

    public GeneralService(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    /**
     * Получает аккаунт администратора, если пользователь не является клиентом
     *
     * @param sessionId сессия пользователя
     * @return аккаунт администратора
     * @throws ServiceException если пользователь не администратор
     */
    Account getAdmin(String sessionId) throws ServiceException {
        Account account = getAccount(sessionId);

        if (!account.isAdmin()) {
            throw new ServiceException(ServiceException.ErrorCode.NOT_ADMIN);
        }

        return account;
    }

    /**
     * Получает аккаунт клиента, если пользователь не является администратором
     *
     * @param sessionId сессия пользователя
     * @return аккаунт клиента
     * @throws ServiceException если пользователь является администратором
     */
    Account getClient(String sessionId) throws ServiceException {
        Account account = getAccount(sessionId);

        if (account.isAdmin()) {
            throw new ServiceException(ServiceException.ErrorCode.NOT_CLIENT);
        }

        return account;
    }

    /**
     * Получает аккаунт
     *
     * @param sessionId сессия пользователя
     * @return аккаунт пользователя
     * @throws ServiceException если сессия недействительна или отсутствует
     */
    public Account getAccount(String sessionId) throws ServiceException {
        Session session = sessionDao.get(sessionId);

        if (session == null) {
            throw new ServiceException(ServiceException.ErrorCode.NOT_LOGIN);
        }

        return session.getAccount();
    }
}
