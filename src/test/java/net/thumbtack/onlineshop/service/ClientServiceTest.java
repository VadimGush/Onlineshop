package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.BasketDao;
import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.*;
import net.thumbtack.onlineshop.dto.AccountDto;
import net.thumbtack.onlineshop.dto.ProductDto;
import net.thumbtack.onlineshop.dto.ResultBasketDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class ClientServiceTest {

    private ClientService clientService;

    @Mock
    private AccountDao mockAccountDao;

    @Mock
    private SessionDao mockSessionDao;

    @Mock
    private ProductDao mockProductDao;

    @Mock
    private BasketDao mockBasketDao;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);
        clientService = new ClientService(
                mockAccountDao,
                mockSessionDao,
                mockProductDao,
                mockBasketDao);
    }

    @Test()
    public void testPutDeposit() throws ServiceException {

        Account client = generateClient();
        client.setDeposit(9);

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        AccountDto result = clientService.putDeposit("token", 12);

        verify(mockAccountDao).update(client);

        assertEquals(21, (int)result.getDeposit());

    }

    @Test
    public void testBuyProduct1() throws ServiceException {

        // Клиент покупает ровно весь товар, который есть

        Account client = generateClient();
        client.setDeposit(52);

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        Product product = new Product(
                "product", 5,10
        );

        when(mockProductDao.get(0)).thenReturn(product);

        ProductDto request = new ProductDto(0L, "product", 10, 5);
        ProductDto result = clientService.buyProduct("token", request);

        // Количество товара должно будет изменится
        verify(mockProductDao).update(product);
        verify(mockProductDao, never()).delete(product);
        // А запись о клиенте должна изменится
        verify(mockAccountDao).update(client);

        assertEquals(request.getId(), result.getId());
        assertEquals(request.getName(), result.getName());
        assertEquals(request.getPrice(), result.getPrice());
        assertEquals(request.getCount(), result.getCount());
        assertEquals(2, (int)client.getDeposit());

    }

    @Test
    public void testBuyProduct2() throws ServiceException {

        // Клиент выкупает часть товара

        Account client = generateClient();
        client.setDeposit(52);

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        Product product = new Product(
                "product", 5,10
        );

        when(mockProductDao.get(0)).thenReturn(product);

        ProductDto request = new ProductDto(0L, "product", 10, 2);
        ProductDto result = clientService.buyProduct("token", request);

        // Количество товара просто должно изменится
        verify(mockProductDao).update(product);
        verify(mockProductDao, never()).delete(product);
        // А запись о клиенте должна изменится
        verify(mockAccountDao).update(client);

        assertEquals(request.getId(), result.getId());
        assertEquals(request.getName(), result.getName());
        assertEquals(request.getPrice(), result.getPrice());
        assertEquals(request.getCount(), result.getCount());
        assertEquals(32, (int)client.getDeposit());

        // На складе осталось три товара
        assertEquals(3, (int)product.getCount());

    }

    @Test(expected = ServiceException.class)
    public void testBuyProductWithWrongId() throws ServiceException {

        Account client = generateClient();
        client.setDeposit(52);

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        Product product = new Product(
                "product", 5,10
        );

        when(mockProductDao.get(0)).thenReturn(product);

        try {
            ProductDto request = new ProductDto(1L, "product", 10, 2);
            clientService.buyProduct("token", request);
        } catch (ServiceException e) {
            verify(mockProductDao, never()).delete(any());
            verify(mockProductDao, never()).update(any());
            verify(mockAccountDao, never()).update(any());
            assertEquals(ServiceException.ErrorCode.PRODUCT_NOT_FOUND, e.getErrorCode());
            throw e;
        }

    }

    @Test(expected = ServiceException.class)
    public void testAddToBasketWithWrongId() throws ServiceException {

        Account client = generateClient();

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        Product product = new Product(
                "product", 5,10
        );

        when(mockProductDao.get(0)).thenReturn(product);

        try {
            ProductDto request = new ProductDto(1L, "product", 10, 2);
            clientService.addToBasket("token", request);
        } catch (ServiceException e) {
            verify(mockProductDao, never()).delete(any());
            verify(mockProductDao, never()).update(any());
            verify(mockAccountDao, never()).update(any());
            verify(mockBasketDao, never()).insert(any());
            assertEquals(ServiceException.ErrorCode.PRODUCT_NOT_FOUND, e.getErrorCode());
            throw e;
        }

    }

    @Test(expected = ServiceException.class)
    public void testAddToBasketWithWrongName() throws ServiceException {

        Account client = generateClient();

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        Product product = new Product(
                "product", 5,10
        );

        when(mockProductDao.get(0)).thenReturn(product);

        try {
            ProductDto request = new ProductDto(0L, "product2", 10, 2);
            clientService.addToBasket("token", request);
        } catch (ServiceException e) {
            verify(mockProductDao, never()).delete(any());
            verify(mockProductDao, never()).update(any());
            verify(mockAccountDao, never()).update(any());
            verify(mockBasketDao, never()).insert(any());
            assertEquals(ServiceException.ErrorCode.WRONG_PRODUCT_INFO, e.getErrorCode());
            throw e;
        }

    }

    @Test(expected = ServiceException.class)
    public void testAddToBasketWithWrongPrice() throws ServiceException {

        Account client = generateClient();

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        Product product = new Product(
                "product", 5,10
        );

        when(mockProductDao.get(0)).thenReturn(product);

        try {
            ProductDto request = new ProductDto(0L, "product", 9, 2);
            clientService.addToBasket("token", request);
        } catch (ServiceException e) {
            verify(mockProductDao, never()).delete(any());
            verify(mockProductDao, never()).update(any());
            verify(mockAccountDao, never()).update(any());
            verify(mockBasketDao, never()).insert(any());
            assertEquals(ServiceException.ErrorCode.WRONG_PRODUCT_INFO, e.getErrorCode());
            throw e;
        }

    }

    @Test(expected = ServiceException.class)
    public void testBuyProductWithWrongName() throws ServiceException {

        Account client = generateClient();
        client.setDeposit(52);

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        Product product = new Product(
                "product", 5,10
        );

        when(mockProductDao.get(0)).thenReturn(product);

        try {
            ProductDto request = new ProductDto(0L, "product2", 10, 2);
            clientService.buyProduct("token", request);
        } catch (ServiceException e) {
            verify(mockProductDao, never()).delete(any());
            verify(mockProductDao, never()).update(any());
            verify(mockAccountDao, never()).update(any());
            assertEquals(ServiceException.ErrorCode.WRONG_PRODUCT_INFO, e.getErrorCode());
            throw e;
        }

    }

    @Test(expected = ServiceException.class)
    public void testBuyProductWithWrongPrice() throws ServiceException {

        Account client = generateClient();
        client.setDeposit(52);

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        Product product = new Product(
                "product", 5,10
        );

        when(mockProductDao.get(0)).thenReturn(product);

        try {
            ProductDto request = new ProductDto(0L, "product", 12, 2);
            clientService.buyProduct("token", request);
        } catch (ServiceException e) {
            verify(mockProductDao, never()).delete(any());
            verify(mockProductDao, never()).update(any());
            verify(mockAccountDao, never()).update(any());
            assertEquals(ServiceException.ErrorCode.WRONG_PRODUCT_INFO, e.getErrorCode());
            throw e;
        }

    }

    @Test(expected = ServiceException.class)
    public void testBuyProductWithWrongCount() throws ServiceException {
        // На складе не хватает товара
        Account client = generateClient();
        client.setDeposit(52);

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        Product product = new Product(
                "product", 5,10
        );

        when(mockProductDao.get(0)).thenReturn(product);

        try {
            ProductDto request = new ProductDto(0L, "product", 10, 6);
            clientService.buyProduct("token", request);
        } catch (ServiceException e) {
            verify(mockProductDao, never()).delete(any());
            verify(mockProductDao, never()).update(any());
            verify(mockAccountDao, never()).update(any());
            assertEquals(ServiceException.ErrorCode.NOT_ENOUGH_PRODUCT, e.getErrorCode());
            throw e;
        }
    }


    @Test(expected = ServiceException.class)
    public void testBuyProductWithWrongDeposit() throws ServiceException {

        // У клиента не достаточно денег для покупки товара

        Account client = generateClient();
        client.setDeposit(10);

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        Product product = new Product(
                "product", 5,10
        );

        when(mockProductDao.get(0)).thenReturn(product);

        try {
            ProductDto request = new ProductDto(0L, "product", 10, 5);
            clientService.buyProduct("token", request);
        } catch (ServiceException e) {
            verify(mockProductDao, never()).delete(any());
            verify(mockProductDao, never()).update(any());
            verify(mockAccountDao, never()).update(any());
            assertEquals(ServiceException.ErrorCode.NOT_ENOUGH_MONEY, e.getErrorCode());
            throw e;
        }
    }

    @Test
    public void testAddToBasket() throws ServiceException {

        // Клиент добавляет товар в корзину

        Account client = generateClient();

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        Product product = new Product(
                "product", 5,10
        );
        product.setId(0L);

        when(mockProductDao.get(0)).thenReturn(product);

        // По требованиям он может добавить сколько угодно товара
        // на какую угодно сумму вне зависимости от его депозита
        ProductDto request = new ProductDto(0L, "product", 10, 50);

        Basket basket = new Basket(client, product, request.getCount());
        when(mockBasketDao.get(client)).thenReturn(Arrays.asList(basket));

        List<ProductDto> result = clientService.addToBasket("token", request);

        // Должна была быть вставка одно единственного товара в корзину
        verify(mockBasketDao).insert(any());

        // Без каких-либо изменений на стороне клиентов или склада
        verify(mockAccountDao, never()).update(any());
        verify(mockProductDao, never()).update(any());
        verify(mockProductDao, never()).delete(any());

        assertEquals(1, result.size());

        ProductDto fromBasket = result.get(0);
        assertEquals(request.getName(), fromBasket.getName());
        assertEquals((int)request.getPrice(), (int)fromBasket.getPrice());
        assertEquals((int)request.getCount(), (int)result.get(0).getCount());

    }

    @Test
    public void testAddToBasketWithSameProduct() throws ServiceException {

        // Клиент добавляет товар в корзину
        // Но предполагаем, что товар уже был в корзине, а значит его количество
        // просто должно прибавится

        Account client = generateClient();

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));
        Product product = new Product("product", 5,10);
        product.setId(0L);
        when(mockProductDao.get(0)).thenReturn(product);
        Basket basket = new Basket(client, product, 3);
        when(mockBasketDao.get(client, 0)).thenReturn(basket);

        // По требованиям он может добавить сколько угодно товара
        // на какую угодно сумму вне зависимости от его депозита
        ProductDto request = new ProductDto(0L, "product", 10, 50);

        // Должен вернуть одну запись из корзины с прибавленным количеством
        when(mockBasketDao.get(client)).thenReturn(Arrays.asList(
                new Basket(client, product, request.getCount() + basket.getCount())
        ));

        List<ProductDto> result = clientService.addToBasket("token", request);

        // Проверяем, что у полученного из БД записи корзины изменилось количество
        assertEquals(53, (int)basket.getCount());

        // Количество должно изменится, но никаких вставок в БД
        verify(mockBasketDao).update(basket);
        verify(mockBasketDao, never()).insert(basket);

        // Без каких-либо изменений на стороне клиентов или склада
        verify(mockAccountDao, never()).update(any());
        verify(mockProductDao, never()).update(any());
        verify(mockProductDao, never()).delete(any());

        assertEquals(1, result.size());

        ProductDto fromBasket = result.get(0);
        assertEquals(request.getName(), fromBasket.getName());
        assertEquals((int)request.getPrice(), (int)fromBasket.getPrice());
        assertEquals(53, (int)result.get(0).getCount());
    }

    @Test
    public void testDeleteFromBasket() throws ServiceException {

        Account client = generateClient();
        Product product = new Product("product", 1, 1);
        Basket basket = new Basket(client, product, 2);

        when(mockBasketDao.get(client, 1)).thenReturn(basket);

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        clientService.deleteFromBasket("token", 1);

        verify(mockSessionDao).get("token");
        verify(mockBasketDao).get(client, 1);
        verify(mockBasketDao).delete(basket);

    }

    @Test(expected = ServiceException.class)
    public void testDeleteFromBasketProductNotFound() throws ServiceException {

        Account client = generateClient();

        when(mockBasketDao.get(client, 1)).thenReturn(null);

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        try {
            clientService.deleteFromBasket("token", 1);
        } catch (ServiceException e) {
            verify(mockBasketDao, never()).delete(any());
            assertEquals(ServiceException.ErrorCode.PRODUCT_NOT_FOUND, e.getErrorCode());
            throw e;
        }

    }

    @Test
    public void testEditProductCount() throws ServiceException {

        Account client = generateClient();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));
        Product product = new Product("product", 1, 1);
        product.setId(0L);
        Basket basket = new Basket(client, product, 3);
        when(mockBasketDao.get(client, 0)).thenReturn(basket);

        when(mockBasketDao.get(client)).thenReturn(Arrays.asList(
                new Basket(client, product, 10)
        ));

        List<ProductDto> result = clientService.editProductCount("token", new ProductDto(
                0L, "product", 1, 10
        ));

        assertEquals(10, (int)basket.getCount());
        verify(mockBasketDao).update(basket);
        verify(mockBasketDao).get(client);

        assertEquals(1, result.size());
        assertEquals(10, (int)result.get(0).getCount());

    }

    @Test(expected = ServiceException.class)
    public void testEditProductCountWithWrongId() throws ServiceException {

        Account client = generateClient();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));
        Product product = new Product("product", 1, 1);
        product.setId(0L);
        Basket basket = new Basket(client, product, 3);
        when(mockBasketDao.get(client, 0)).thenReturn(basket);

        when(mockBasketDao.get(client)).thenReturn(Arrays.asList(
                new Basket(client, product, 10)
        ));

        try {
            clientService.editProductCount("token", new ProductDto(
                    1L, "product", 1, 10
            ));
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.PRODUCT_NOT_FOUND, e.getErrorCode());
            verify(mockBasketDao, never()).update(basket);
            throw e;
        }
    }

    @Test(expected = ServiceException.class)
    public void testEditProductCountWithWrongName() throws ServiceException {

        Account client = generateClient();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));
        Product product = new Product("product", 1, 1);
        product.setId(0L);
        Basket basket = new Basket(client, product, 3);
        when(mockBasketDao.get(client, 0)).thenReturn(basket);

        when(mockBasketDao.get(client)).thenReturn(Arrays.asList(
                new Basket(client, product, 10)
        ));

        try {
            clientService.editProductCount("token", new ProductDto(
                    0L, "product2", 1, 10
            ));
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.WRONG_PRODUCT_INFO, e.getErrorCode());
            verify(mockBasketDao, never()).update(basket);
            throw e;
        }
    }


    @Test(expected = ServiceException.class)
    public void testEditProductCountWithWrongPrice() throws ServiceException {

        Account client = generateClient();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));
        Product product = new Product("product", 1, 1);
        product.setId(0L);
        Basket basket = new Basket(client, product, 3);
        when(mockBasketDao.get(client, 0)).thenReturn(basket);

        when(mockBasketDao.get(client)).thenReturn(Arrays.asList(
                new Basket(client, product, 10)
        ));

        try {
            clientService.editProductCount("token", new ProductDto(
                    0L, "product", 0, 10
            ));
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.WRONG_PRODUCT_INFO, e.getErrorCode());
            verify(mockBasketDao, never()).update(basket);
            throw e;
        }
    }

    @Test
    public void testGetBasket() throws ServiceException {

        Account client = generateClient();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        Product product1 = new Product("product", 1, 1);
        Product product2 = new Product("product", 1, 1);
        List<Basket> basket = Arrays.asList(
                new Basket(client, product1, 1),
                new Basket(client, product2, 1));

        when(mockBasketDao.get(client)).thenReturn(basket);

        List<ProductDto> result = clientService.getBasket("token");

        assertEquals(basket.size(), result.size());
    }

    @Test
    public void testBuyBasket() throws ServiceException {

        Account client = generateClient();
        client.setDeposit(30_024);
        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));


        // Подготавливем список продуктов
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 8; ++i) {
            Product product = new Product("product" + i, 10, 1000);
            product.setId((long)i);
            products.add(product);
            when(mockProductDao.get((long)i)).thenReturn(product);
        }

        // Один товар удалён из БД
        products.get(7).setDeleted(true);

        // Каждый из продуктов добавляем в корзину пользователя
        List<Basket> basket = new ArrayList<>();
        for (int i = 0; i < 8; ++i) {
            Basket basketEntity = new Basket(client, products.get(i), 10);
            basket.add(basketEntity);
            when(mockBasketDao.get(client, i)).thenReturn(basketEntity);
        }
        when(mockBasketDao.get(client)).thenReturn(basket);

        // Четвёртого продукта должно будет не хватать на складе
        basket.get(3).setCount(20);

        // Теперь формируем список покупок
        List<ProductDto> toBuy = new LinkedList<>(Arrays.asList(
                // Данный продукт останется в запросе
                new ProductDto(0L, "product0", 1000, 9),

                // --- Далее список товаров, которые должны будут исключится из списка покупок
                // Продукт с неверным id
                new ProductDto(11L, "product", 1000, 10),
                // Продукт с неверным именем
                new ProductDto(1L, "product0", 1000, 10),
                // Продукт с неверной ценою
                new ProductDto(2L, "product2", 21, 10),
                // Продукт, которого не достаточно на складе
                new ProductDto(3L, "product3", 1000, 20),

                // --- Далее товары, которые останутся в списке покупок, но изменят своё количество
                // Этого товара больше чем в корзине
                new ProductDto(4L, "product4", 1000, 15),
                // Этот товар без количества совсем
                new ProductDto(5L, "product5", 1000, null),
                // Товар был удалён администратором
                new ProductDto(7L, "product7", 1000, 5)
        ));

        // Итого у нас будет куплено 3 наименования общим количеством 29 единиц за 1000 за штуку
        // Того клиент потратит 29 000, а из БД изменятся всего три товара

        ResultBasketDto result = clientService.buyBasket("token", toBuy);

        // Проверим сначала вызовы и информацию о клиенте
        assertEquals(1024, (int)client.getDeposit());
        // Запись о клиенте должна была обновится
        verify(mockAccountDao).update(client);
        // И три записи о товаре должны были тоже изменится
        verify(mockProductDao, times(3)).update(any());
        // И должны были удалится две записи из корзины (потому что весь товар выкуплен)
        verify(mockBasketDao, times(2)).delete(any());
        // А одна запись в корзине всего лишь поменяет количество
        verify(mockBasketDao).update(any());

        List<ProductDto> bought = result.getBought();

        // Проверяем список купленных продуктов
        // Там их трое
        assertEquals(3, bought.size());
    }

    @Test(expected = ServiceException.class)
    public void testBuyBasketNotEnoughMoney() throws ServiceException {

        Account client = generateClient();
        client.setDeposit(2000);
        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        // Подготавливем список продуктов
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 7; ++i) {
            Product product = new Product("product" + i, 10, 1000);
            product.setId((long)i);
            products.add(product);
            when(mockProductDao.get((long)i)).thenReturn(product);
        }

        // Каждый из продуктов добавляем в корзину пользователя
        List<Basket> basket = new ArrayList<>();
        for (int i = 0; i < 7; ++i) {
            Basket basketEntity = new Basket(client, products.get(i), 10);
            basket.add(basketEntity);
            when(mockBasketDao.get(client, i)).thenReturn(basketEntity);
        }
        when(mockBasketDao.get(client)).thenReturn(basket);

        // Четвёртого продукта должно будет не хватать на складе
        basket.get(3).setCount(20);

        // Теперь формируем список покупок
        List<ProductDto> toBuy = new LinkedList<>(Arrays.asList(
                // Данный продукт останется в запросе
                new ProductDto(0L, "product0", 1000, 10),

                // --- Далее список товаров, которые должны будут исключится из списка покупок
                // Продукт с неверным id
                new ProductDto(11L, "product", 1000, 10),
                // Продукт с неверным именем
                new ProductDto(1L, "product0", 1000, 10),
                // Продукт с неверной ценою
                new ProductDto(2L, "product2", 21, 10),
                // Продукт, которого не достаточно на складе
                new ProductDto(3L, "product3", 1000, 20),

                // --- Далее товары, которые останутся в списке покупок, но изменят своё количество
                // Этого товара больше чем в корзине
                new ProductDto(4L, "product4", 1000, 15),
                // Этот товар без количества совсем
                new ProductDto(5L, "product5", 1000, null)
        ));

        try {
            clientService.buyBasket("token", toBuy);

        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_ENOUGH_MONEY, e.getErrorCode());

            verify(mockAccountDao, never()).update(any());
            verify(mockBasketDao, never()).update(any());
            verify(mockBasketDao, never()).delete(any());
            verify(mockProductDao, never()).update(any());
            verify(mockProductDao, never()).delete(any());

            throw e;
        }

    }

    @Test
    public void testAccountIsAdmin() {

        when(mockSessionDao.get("token")).thenReturn(new Session("token",
                AccountFactory.createAdmin("er", "erw", "ewrwe", "erwe", "wer")
        ));

        // Что-то какой-то ужасный тест
        // Наверное можно поправитьс с помощью Java Reflection, но я пока не знаю как

        try {
            clientService.putDeposit("token", 0);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_CLIENT, e.getErrorCode());
        }

        try {
            clientService.buyProduct("token", null);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_CLIENT, e.getErrorCode());
        }

        try {
            clientService.addToBasket("token", null);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_CLIENT, e.getErrorCode());
        }

        try {
            clientService.deleteFromBasket("token", 0);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_CLIENT, e.getErrorCode());
        }

        try {
            clientService.editProductCount("token", null);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_CLIENT, e.getErrorCode());
        }

        try {
            clientService.getBasket("token");
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_CLIENT, e.getErrorCode());
        }
    }

    @Test
    public void testWrongSessionId() {

        when(mockSessionDao.get("token")).thenReturn(null);

        // Что-то какой-то ужасный тест
        // Наверное можно поправитьс с помощью Java Reflection, но я пока не знаю как

        try {
            clientService.putDeposit("token", 0);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
        }

        try {
            clientService.buyProduct("token", null);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
        }

        try {
            clientService.addToBasket("token", null);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
        }

        try {
            clientService.deleteFromBasket("token", 0);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
        }

        try {
            clientService.editProductCount("token", null);
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
        }

        try {
            clientService.getBasket("token");
            fail();
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
        }
    }


    private Account generateClient() {
        return AccountFactory.createClient(
                "234324", "234324", "wereww1", "werewr1",
                "wrewf3", "werwer235", "werw23", "ewr23423"
        );
    }
}
