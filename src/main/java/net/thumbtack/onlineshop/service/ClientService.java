package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.BasketDao;
import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.*;
import net.thumbtack.onlineshop.dto.BuyProductDto;
import net.thumbtack.onlineshop.dto.ClientDto;
import net.thumbtack.onlineshop.dto.ClientEditDto;
import net.thumbtack.onlineshop.request.ItemBuyRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private AccountDao clientDao;
    private SessionDao sessionDao;
    private ProductDao productDao;
    private BasketDao basketDao;

    public ClientService(
            AccountDao clientDao,
            SessionDao sessionDao,
            ProductDao productDao,
            BasketDao basketDao) {
        this.clientDao = clientDao;
        this.sessionDao = sessionDao;
        this.productDao = productDao;
        this.basketDao = basketDao;
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
     * @param sessionId сессия клиента
     * @param amount количество денег
     */
    public Account putDeposit(String sessionId, int amount) throws ServiceException {

        Account account = getAccount(sessionId);
        account.setDeposit(account.getDeposit() + amount);
        clientDao.update(account);

        return account;
    }

    /**
     * Запросить количество денег на счету
     * @param sessionId сессия клиента
     * @return полная информация об аккаунте клиента
     * @throws ServiceException
     */
    public Account getDeposit(String sessionId) throws ServiceException {
        return getAccount(sessionId);
    }

    /**
     * Купить некоторый товар
     * @param sessionId сессия клиента
     * @param buyProduct информиация о товаре
     * @return информация о купленном товаре
     * @throws ServiceException
     */
    public BuyProductDto buyProduct(String sessionId, BuyProductDto buyProduct) throws ServiceException {

        Account account = getAccount(sessionId);
        Product product = productDao.get(buyProduct.getId());

        checkProducts(product, buyProduct);

        if (buyProduct.getCount() > product.getCount())
            throw new ServiceException(ServiceException.ErrorCode.NOT_ENOUGH_PRODUCT, "count");

        // Очень спорный вопрос по поводу того, какое поле стало причиной такой ошибки
        if (buyProduct.getCount() * buyProduct.getPrice() > account.getDeposit())
            throw new ServiceException(ServiceException.ErrorCode.NOT_ENOUGH_MONEY, "price");

        if (product.getCount() == buyProduct.getCount())
            productDao.delete(product);
        else if (product.getCount() > buyProduct.getCount()) {
            product.setCount(product.getCount() - buyProduct.getCount());
            productDao.update(product);
        }

        account.setDeposit(account.getDeposit() - buyProduct.getCount() * buyProduct.getPrice());
        clientDao.update(account);

        return buyProduct;

    }

    /**
     * Добавляет товар в корзину
     *
     * <b>Внимание:</b> в записи корзины указывается ссылка на продукт в БД и рядом поле
     * с количеством этого продукта, который заказывал клиент. Получается что у нас
     * два поля с количеством. Один из таблицы product, который говорит сколько у нас
     * товара на складе, а другой в таблице basket, который говорит
     * сколько товара клиент положил в свою корзину.
     *
     * Поэтому количество товара в корзине надо получать только через Basket.getCount()
     *
     * @param sessionId сессия клиента
     * @param buyProduct информация о товаре
     * @return информация о купленном товаре
     * @throws ServiceException
     */
    public List<Basket> addToBasket(String sessionId, BuyProductDto buyProduct) throws ServiceException {

        Account account = getAccount(sessionId);
        Product product = productDao.get(buyProduct.getId());

        checkProducts(product, buyProduct);

        Basket basket = new Basket(account, product, buyProduct.getCount());
        basketDao.insert(basket);

        return basketDao.get(account);

    }

    public void deleteFromBasket(String sessionId, Product product) {

    }

    public void editProductCount(String sessionId, Product product) {

    }

    public void getBasket(String sessionId) {

    }

    public void buyBasket(String sessionId) {

    }

    private void checkProducts(Product product, BuyProductDto buyProduct) throws ServiceException {
        if (product == null)
            throw new ServiceException(ServiceException.ErrorCode.PRODUCT_NOT_FOUND, "id");

        if (!product.getName().equals(buyProduct.getName()))
            throw new ServiceException(ServiceException.ErrorCode.WRONG_PRODUCT_INFO, "name");

        if (product.getPrice() != buyProduct.getPrice())
            throw new ServiceException(ServiceException.ErrorCode.WRONG_PRODUCT_INFO, "price");
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
