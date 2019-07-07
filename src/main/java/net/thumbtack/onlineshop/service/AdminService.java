package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.database.models.AccountFactory;
import net.thumbtack.onlineshop.database.models.Session;
import net.thumbtack.onlineshop.dto.AdminDto;
import net.thumbtack.onlineshop.dto.AdminEditDto;
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

    /**
     * @param admin регистрационная инфа об админе
     * @return аккаунт зарегистрированного администратора
     * @throws ServiceException
     */
    public Account register(AdminDto admin) throws ServiceException {
        if (accountDao.exists(admin.getLogin()))
            throw new ServiceException(ServiceException.ErrorCode.LOGIN_ALREADY_IN_USE, "login");

        Account registeredAdmin = AccountFactory.createAdmin(
                admin.getFirstName(), admin.getLastName(), admin.getPatronymic(), admin.getPosition(), admin.getLogin(), admin.getPassword()
        );
        accountDao.insert(registeredAdmin);
        return registeredAdmin;
    }

    /**
     * @param sessionId сессия администратора
     * @param admin запрос с изменёнными данными
     * @return аккаунт изменённого администратора
     * @throws ServiceException
     */
    public Account edit(String sessionId, AdminEditDto admin) throws ServiceException {

        Account account = getAdmin(sessionId);

        if (!account.getPassword().equals(admin.getOldPassword()))
            throw new ServiceException(ServiceException.ErrorCode.WRONG_PASSWORD, "oldPassword");

        account.setFirstName(admin.getFirstName());
        account.setLastName(admin.getLastName());
        account.setPatronymic(admin.getPatronymic());
        account.setPassword(admin.getNewPassword());
        account.setPosition(admin.getPosition());

        accountDao.update(account);
        return account;
    }

    /**
     * @param sessionId сессия администратора
     * @return список всех клиентов
     * @throws ServiceException
     */
    public List<Account> getAll(String sessionId) throws ServiceException {

        getAdmin(sessionId);

        return accountDao.getClients();
    }

    /**
     * Получает аккаунт админа из БД и проверяет что он и вправду
     * является администратором
     * @param sessionId сессия администратора
     * @return аккаунт администратора
     * @throws ServiceException
     */
    private Account getAdmin(String sessionId) throws ServiceException {
        Session session = sessionDao.get(sessionId);

        if (session == null)
            throw new ServiceException(ServiceException.ErrorCode.NOT_LOGIN, "JAVASESSIONID");

        Account account = session.getAccount();

        if (!account.isAdmin())
            throw new ServiceException(ServiceException.ErrorCode.NOT_ADMIN, "JAVASESSIONID");

        return account;
    }

}
