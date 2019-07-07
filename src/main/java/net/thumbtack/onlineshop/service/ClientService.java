package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.BasketDao;
import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.*;
import net.thumbtack.onlineshop.dto.BuyProductDto;
import net.thumbtack.onlineshop.dto.ClientDto;
import net.thumbtack.onlineshop.dto.ClientEditDto;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        account.setLastName(client.getLastName());
        account.setPatronymic(client.getPatronymic());
        account.setEmail(client.getEmail());
        account.setAddress(client.getAddress());
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

        if (buyProduct.getCount() * buyProduct.getPrice() > account.getDeposit())
            throw new ServiceException(ServiceException.ErrorCode.NOT_ENOUGH_MONEY);

        product.setCount(product.getCount() - buyProduct.getCount());
        productDao.update(product);

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
     * (Никакого Basket.getProduct().getCount() - это количество товара на складе!)
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

        Basket already = basketDao.get(account, product.getId());

        if (already != null) {
            // Если продукт уже добавлен в корзину, то просто прибавим его количество

            already.setCount(already.getCount() + buyProduct.getCount());
            basketDao.update(already);

        } else {
            // Если продукта в корзине нет, то добавляем его

            Basket basket = new Basket(account, product, buyProduct.getCount());
            basketDao.insert(basket);
        }

        return basketDao.get(account);

    }

    /**
     * Удаляет товар из корзины
     * @param sessionId сессия клиента
     * @param productId id продукта
     * @throws ServiceException
     */
    public void deleteFromBasket(String sessionId, long productId) throws ServiceException {

        Account account = getAccount(sessionId);
        Basket basket = basketDao.get(account, productId);

        if (basket == null)
            throw new ServiceException(ServiceException.ErrorCode.PRODUCT_NOT_FOUND);

        basketDao.delete(basket);

    }


    /**
     * Изменяет количество товара в корзине
     * @param sessionId сессия клиент
     * @param product информация о товаре
     * @return содержание корзины
     * @throws ServiceException
     */
    public List<Basket> editProductCount(String sessionId, BuyProductDto product) throws ServiceException {

        Account account = getAccount(sessionId);
        Basket basket = basketDao.get(account, product.getId());

        if (basket == null)
            throw new ServiceException(ServiceException.ErrorCode.PRODUCT_NOT_FOUND, "id");

        checkProducts(basket.getProduct(), product);
        basket.setCount(product.getCount());
        basketDao.update(basket);

        return basketDao.get(account);
    }

    /**
     * Получает содержимое корзины клиента
     * @param sessionId сессия клиента
     * @return содержимое корзины
     * @throws ServiceException
     */
    public List<Basket> getBasket(String sessionId) throws ServiceException {

        Account account = getAccount(sessionId);

        return basketDao.get(account);

    }


    /**
     * Выкупает товар из корзины
     * @param sessionId сессия клиента
     * @param toBuy список товаров для покупки
     *
     * @return пара из двух коллекций. Первая коллекция содержит список купленных товаров, а
     * вторая коллекция содержит список оставшихся в корзине товаров
     *
     * @throws ServiceException
     */
    public Pair<List<BuyProductDto>, List<Basket>> buyBasket(String sessionId, List<BuyProductDto> toBuy) throws ServiceException {

        // Внимание: вносить изменения только с ТЗ в руках!

        Account account = getAccount(sessionId);
        List<Basket> basket = basketDao.get(account);

        List<BuyProductDto> copyList = new ArrayList<>(toBuy);

        // Если инфа у товара в списке неверная, то просто не обрабатываем его
        for (BuyProductDto product : copyList) {

            // Ищем продукт с таким id в корзине
            Basket basketEntity = null;
            for (Basket entity : basket) {
                if (entity.getProduct().getId() == product.getId()) {
                    basketEntity = entity;
                    break;
                }
            }

            // Если продукта в корзине нет, то и выкидываем его из запроса
            if (basketEntity == null) {
                toBuy.remove(product);
                continue;
            }

            // Теперь сверяем что данные с бд совпадают
            // Еси нет, то выкидываем из список покупок
            try {
                checkProducts(basketEntity.getProduct(), product);
            } catch (ServiceException e) {
                toBuy.remove(product);
                continue;
            }

            // Если количество не указано, то берём количество из корзины
            // так же если количество больше чем в корзине
            if (product.getCount() == null || product.getCount() > basketEntity.getCount())
                product.setCount(basketEntity.getCount());

            // Если количество товара на складе меньше чем мы хотим купить,
            // то опять выкидываем из списка покупок
            if (product.getCount() > basketEntity.getProduct().getCount())
                toBuy.remove(product);

        }

        // Теперь в списке tobBuy всё валидное
        // Теперь считаем сколько денег нужно для покупки всего
        int sum = 0;
        for (BuyProductDto product : toBuy) {
            sum += product.getCount() * product.getPrice();
        }

        if (sum > account.getDeposit())
            throw new ServiceException(ServiceException.ErrorCode.NOT_ENOUGH_MONEY);

        // Теперь можно всё покупать
        // Тут можно было бы использовать метод buyProduct, но это и так увеличит количество
        // исключений и в без того "исключительном" коде

        // Снимаем деньги
        account.setDeposit(account.getDeposit() - sum);
        clientDao.update(account);

        for (BuyProductDto product : toBuy) {

            // Уменьшаем количество товаров на складе
            Product currentProduct = productDao.get(product.getId());
            currentProduct.setCount(currentProduct.getCount() - product.getCount());
            productDao.update(currentProduct);

            // Удаляем нужное количество товара из корзины
            // Или совсем из корзины
            Basket basketEntity = basketDao.get(account, product.getId());
            basketEntity.setCount(basketEntity.getCount() - product.getCount());
            if (basketEntity.getCount() == 0)
                basketDao.delete(basketEntity);
            else
                basketDao.update(basketEntity);

            // Ну вроде бы всё...
        }

        return Pair.of(toBuy, basketDao.get(account));
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
