package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.database.models.AccountFactory;
import net.thumbtack.onlineshop.database.models.Product;
import net.thumbtack.onlineshop.database.models.Session;
import net.thumbtack.onlineshop.dto.ClientDto;
import net.thumbtack.onlineshop.dto.ClientEditDto;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private AccountDao clientDao;
    private SessionDao sessionDao;

    public ClientService(AccountDao clientDao, SessionDao sessionDao) {
        this.clientDao = clientDao;
        this.sessionDao = sessionDao;
    }

    /**
     * Регистрирует нового клиента
     * @param client информация о клиенте
     * @return созданный аккаунт клиента
     * @throws ServiceException
     */
    public Account register(ClientDto client) throws ServiceException {

        if (clientDao.exists(client.getLogin()))
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
        clientDao.insert(registeredClient);
        return registeredClient;

    }

    /**
     * Изменяет информацию о клиенте
     * @param sessionId сессия клиента
     * @param client новая инфа клиента
     * @return аккаунт изменённого клиента
     * @throws ServiceException
     */
    public Account edit(String sessionId, ClientEditDto client) throws ServiceException {

        Account account = getAccount(sessionId);

        if (!account.getPassword().equals(client.getOldPassword()))
            throw new ServiceException(ServiceException.ErrorCode.WRONG_PASSWORD, "oldPassword");

        account.setFirstName(client.getFirstName());
        account.setSecondName(client.getLastName());
        account.setThirdName(client.getPatronymic());
        account.setEmail(client.getEmail());
        account.setPostAddress(client.getAddress());
        account.setPhone(client.getPhone());
        account.setPassword(client.getNewPassword());

        clientDao.update(account);

        return account;
    }

    /**
     * Положить деньги на клиентский счёт
     * @param sessionId
     * @param amount
     */
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

    private Account getAccount(String sessionId) throws ServiceException {
        Session session = sessionDao.get(sessionId);

        if (session == null)
            throw new ServiceException(ServiceException.ErrorCode.NOT_LOGIN, "JAVASESSIONID");

        Account account = session.getAccount();

        if (account.isAdmin())
            throw new ServiceException(ServiceException.ErrorCode.NOT_CLIENT, "JAVASESSIONID");

        return account;
    }

}
