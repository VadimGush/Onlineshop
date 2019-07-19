package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.BasketDao;
import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.database.models.Basket;
import net.thumbtack.onlineshop.database.models.Product;
import net.thumbtack.onlineshop.dto.AccountDto;
import net.thumbtack.onlineshop.dto.ProductDto;
import net.thumbtack.onlineshop.dto.ResultBasketDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис клиентского функционала
 */
@Service
public class ClientService extends GeneralService {

    private AccountDao accountDao;
    private ProductDao productDao;
    private BasketDao basketDao;

    public ClientService(
            AccountDao accountDao,
            SessionDao sessionDao,
            ProductDao productDao,
            BasketDao basketDao) {
        super(sessionDao);
        this.accountDao = accountDao;
        this.productDao = productDao;
        this.basketDao = basketDao;
    }

    /**
     * Положить деньги на клиентский счёт
     *
     * @param sessionId сессия клиента
     * @param amount    количество денег
     */
    public AccountDto putDeposit(String sessionId, int amount) throws ServiceException {

        Account account = getClient(sessionId);
        account.setDeposit(account.getDeposit() + amount);
        accountDao.update(account);

        return new AccountDto(account);
    }

    /**
     * Купить некоторый товар
     *
     * @param sessionId  сессия клиента
     * @param buyProduct информиация о товаре
     * @return информация о купленном товаре
     */
    public ProductDto buyProduct(String sessionId, ProductDto buyProduct) throws ServiceException {

        Account account = getClient(sessionId);
        Product product = productDao.get(buyProduct.getId());

        compareProducts(product, buyProduct);

        if (buyProduct.getCount() > product.getCount()) {
            throw new ServiceException(ServiceException.ErrorCode.NOT_ENOUGH_PRODUCT, "count");
        }

        if (buyProduct.getCount() * buyProduct.getPrice() > account.getDeposit()) {
            throw new ServiceException(ServiceException.ErrorCode.NOT_ENOUGH_MONEY);
        }

        product.setCount(product.getCount() - buyProduct.getCount());
        productDao.update(product);

        account.setDeposit(account.getDeposit() - buyProduct.getCount() * buyProduct.getPrice());
        accountDao.update(account);

        return buyProduct;

    }

    /**
     * Добавляет товар в корзину
     *
     * @param sessionId  сессия клиента
     * @param buyProduct информация о товаре
     * @return информация о купленном товаре
     */
    public List<ProductDto> addToBasket(String sessionId, ProductDto buyProduct) throws ServiceException {

        Account account = getClient(sessionId);

        if (buyProduct.getId() == null) {
            throw new ServiceException(ServiceException.ErrorCode.PRODUCT_NOT_FOUND, "id");
        }

        Product product = productDao.get(buyProduct.getId());
        compareProducts(product, buyProduct);

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

        List<ProductDto> result = new ArrayList<>();
        basketDao.get(account).forEach(b -> result.add(new ProductDto(b)));

        return result;
    }

    /**
     * Удаляет товар из корзины
     *
     * @param sessionId сессия клиента
     * @param productId id продукта
     */
    public void deleteFromBasket(String sessionId, long productId) throws ServiceException {

        Account account = getClient(sessionId);
        Basket basket = basketDao.get(account, productId);

        if (basket == null) {
            throw new ServiceException(ServiceException.ErrorCode.PRODUCT_NOT_FOUND);
        }

        basketDao.delete(basket);

    }

    /**
     * Изменяет количество товара в корзине
     *
     * @param sessionId сессия клиент
     * @param product   информация о товаре
     * @return содержание корзины
     */
    public List<ProductDto> editProductCount(String sessionId, ProductDto product) throws ServiceException {

        Account account = getClient(sessionId);

        if (product.getId() == null) {
            throw new ServiceException(ServiceException.ErrorCode.PRODUCT_NOT_FOUND, "id");
        }

        Basket basket = basketDao.get(account, product.getId());

        if (basket == null) {
            throw new ServiceException(ServiceException.ErrorCode.PRODUCT_NOT_FOUND, "id");
        }

        if (product.getCount() == null) {
            throw new ServiceException(ServiceException.ErrorCode.REQUIRED_COUNT, "count");
        }

        compareProducts(basket.getProduct(), product);
        basket.setCount(product.getCount());
        basketDao.update(basket);

        List<ProductDto> result = new ArrayList<>();
        basketDao.get(account).forEach(b -> result.add(new ProductDto(b)));

        return result;
    }

    /**
     * Получает содержимое корзины клиента
     *
     * @param sessionId сессия клиента
     * @return содержимое корзины
     */
    public List<ProductDto> getBasket(String sessionId) throws ServiceException {

        Account account = getClient(sessionId);

        List<ProductDto> result = new ArrayList<>();
        basketDao.get(account).forEach(b -> result.add(new ProductDto(b)));

        return result;
    }

    /**
     * Выкупает товар из корзины
     *
     * @param sessionId сессия клиента
     * @param toBuy     список товаров для покупки
     * @return пара из двух коллекций. Первая коллекция содержит список купленных товаров, а
     * вторая коллекция содержит список оставшихся в корзине товаров
     */
    public ResultBasketDto buyBasket(String sessionId, List<ProductDto> toBuy) throws ServiceException {

        Account account = getClient(sessionId);

        // Получаем корзину
        List<Basket> basket = basketDao.get(account);

        // Убираем из списка покупок неверные товары
        removeInvalidProducts(toBuy, basket);

        // Теперь в списке tobBuy всё валидное
        // Теперь считаем сколько денег нужно для покупки всего
        int sum = 0;
        for (ProductDto product : toBuy) {
            sum += product.getCount() * product.getPrice();
        }

        if (sum > account.getDeposit()) {
            throw new ServiceException(ServiceException.ErrorCode.NOT_ENOUGH_MONEY);
        }

        // Начинаем покупать товары

        // Снимаем деньги
        account.setDeposit(account.getDeposit() - sum);
        accountDao.update(account);

        for (ProductDto product : toBuy) {

            // Уменьшаем количество товаров на складе
            Product currentProduct = productDao.get(product.getId());
            currentProduct.setCount(currentProduct.getCount() - product.getCount());
            productDao.update(currentProduct);

            // Удаляем нужное количество товара из корзины
            // Или совсем из корзины
            Basket basketEntity = basketDao.get(account, product.getId());
            basketEntity.setCount(basketEntity.getCount() - product.getCount());
            if (basketEntity.getCount() == 0) {
                basketDao.delete(basketEntity);
            } else {
                basketDao.update(basketEntity);
            }

        }

        // Возвращаем инфу сколько мы купили и сколько в корзине осталось
        return new ResultBasketDto(toBuy, basketDao.get(account));
    }

    /**
     * Убирает из списка покупок все товары, которые не соответсвуют требованиям.
     * (к примеру товары, которых нет в БД или нет в текущей корзине клиента и т.д.)
     *
     * @param toBuy  список покупок
     * @param basket корзина клиента
     */
    private void removeInvalidProducts(List<ProductDto> toBuy, List<Basket> basket) {

        List<ProductDto> copyList = new ArrayList<>(toBuy);

        // Если инфа у товара в списке неверная, то просто не обрабатываем его
        for (ProductDto product : copyList) {

            // Ищем продукт с таким id в корзине
            Basket basketEntity = null;
            for (Basket entity : basket) {
                if (entity.getProduct().getId().equals(product.getId())) {
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
                compareProducts(basketEntity.getProduct(), product);
            } catch (ServiceException e) {
                toBuy.remove(product);
                continue;
            }

            // Если количество не указано, то берём количество из корзины
            // так же если количество больше чем в корзине
            if (product.getCount() == null || product.getCount() > basketEntity.getCount()) {
                product.setCount(basketEntity.getCount());
            }

            // Если количество товара на складе меньше чем мы хотим купить,
            // то опять выкидываем из списка покупок
            if (product.getCount() > basketEntity.getProduct().getCount()) {
                toBuy.remove(product);
                continue;
            }

            // Если товар удалён из БД
            if (basketEntity.getProduct().getDeleted()) {
                toBuy.remove(product);
            }
        }

    }

    /**
     * Сравнивает информацию о товаре из БД с информацией в запросе.
     *
     * @param product    товар из бд
     * @param buyProduct товар из запроса
     * @throws ServiceException если данные о товарах не совпадают
     */
    private void compareProducts(Product product, ProductDto buyProduct) throws ServiceException {
        if (product == null) {
            throw new ServiceException(ServiceException.ErrorCode.PRODUCT_NOT_FOUND, "id");
        }

        if (!product.getName().equals(buyProduct.getName())) {
            throw new ServiceException(ServiceException.ErrorCode.WRONG_PRODUCT_INFO, "name");
        }

        if (!product.getPrice().equals(buyProduct.getPrice())) {
            throw new ServiceException(ServiceException.ErrorCode.WRONG_PRODUCT_INFO, "price");
        }
    }

}
