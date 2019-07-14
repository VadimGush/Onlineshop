package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.database.models.AccountFactory;
import net.thumbtack.onlineshop.database.models.Session;
import net.thumbtack.onlineshop.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService extends GeneralService {

    private AccountDao accountDao;
    private SessionDao sessionDao;

    @Autowired
    public AccountService(AccountDao accountDao, SessionDao sessionDao) {
        super(sessionDao);
        this.accountDao = accountDao;
        this.sessionDao = sessionDao;
    }

    /**
     * Регистрирует нового клиента
     * @param client информация о клиенте
     * @return созданный аккаунт клиента
     */
    public Account register(ClientDto client) throws ServiceException {

        if (accountDao.exists(client.getLogin()))
            throw new ServiceException(ServiceException.ErrorCode.LOGIN_ALREADY_IN_USE, "login");

        Account registeredClient = AccountFactory.createClient(
                client.getFirstName(),
                client.getLastName(),
                client.getPatronymic(),
                client.getEmail(),
                client.getAddress(),
                client.getPhone(),
                client.getLogin(),
                client.getPassword()
        );
        accountDao.insert(registeredClient);
        return registeredClient;

    }

    /**
     * @param admin регистрационная инфа об админе
     * @return аккаунт зарегистрированного администратора
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
     * Изменяет информацию о клиенте
     * @param sessionId сессия клиента
     * @param client новая инфа клиента
     * @return аккаунт изменённого клиента
     */
    public AccountDto edit(String sessionId, ClientEditDto client) throws ServiceException {

        Account account = getClient(sessionId);

        if (!account.getPassword().equals(client.getOldPassword()))
            throw new ServiceException(ServiceException.ErrorCode.WRONG_PASSWORD, "oldPassword");

        account.setFirstName(client.getFirstName());
        account.setLastName(client.getLastName());
        account.setPatronymic(client.getPatronymic());
        account.setEmail(client.getEmail());
        account.setAddress(client.getAddress());
        account.setPhone(client.getPhone());
        account.setPassword(client.getNewPassword());

        accountDao.update(account);

        return new AccountDto(account);
    }

    /**
     * @param sessionId сессия администратора
     * @param admin запрос с изменёнными данными
     * @return аккаунт изменённого администратора
     */
    public AccountDto edit(String sessionId, AdminEditDto admin) throws ServiceException {

        Account account = getAdmin(sessionId);

        if (!account.getPassword().equals(admin.getOldPassword()))
            throw new ServiceException(ServiceException.ErrorCode.WRONG_PASSWORD, "oldPassword");

        account.setFirstName(admin.getFirstName());
        account.setLastName(admin.getLastName());
        account.setPatronymic(admin.getPatronymic());
        account.setPassword(admin.getNewPassword());
        account.setPosition(admin.getPosition());

        accountDao.update(account);
        return new AccountDto(account);
    }

    /**
     * @param sessionId сессия администратора
     * @return список всех клиентов
     */
    public List<AccountDto> getAll(String sessionId) throws ServiceException {

        getAdmin(sessionId);

        List<Account> clients = accountDao.getClients();
        List<AccountDto> result = new ArrayList<>();

        clients.forEach((client) -> result.add(new AccountDto(client, true)));

        return result;
    }

    /**
     * Проводит авторизацию пользователя по логину и паролю
     * @param login пароль пользователя
     * @param password логин пользователя
     * @return идентификатор новой сессии
     */
    public String login(String login, String password) throws ServiceException {
        Account account = accountDao.get(login, password);
        if (account == null)
            throw new ServiceException(ServiceException.ErrorCode.USER_NOT_FOUND);

        Session session = new Session(UUID.randomUUID().toString(), account);
        sessionDao.insert(session);
        return session.getUUID();
    }

    /**
     * Получает информацию об аккаунте по его сессии
     * @param sessionId сессия пользователя
     * @return аккаунт пользователя из БД
     */
    public AccountDto get(String sessionId) throws ServiceException {
        Session session = sessionDao.get(sessionId);

        if (session == null)
            throw new ServiceException(ServiceException.ErrorCode.NOT_LOGIN);

        return new AccountDto(session.getAccount());
    }

    /**
     * Удаляет сессию из БД
     * @param sessionId сессия пользователя
     */
    public void logout(String sessionId) {
        Session session = sessionDao.get(sessionId);

        if (session != null)
            sessionDao.delete(session);
    }

}
