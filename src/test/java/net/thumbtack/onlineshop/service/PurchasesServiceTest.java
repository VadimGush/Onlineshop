package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.domain.dao.AccountDao;
import net.thumbtack.onlineshop.domain.dao.ProductDao;
import net.thumbtack.onlineshop.domain.dao.PurchaseDao;
import net.thumbtack.onlineshop.domain.dao.SessionDao;
import net.thumbtack.onlineshop.domain.models.*;
import net.thumbtack.onlineshop.dto.PurchasesDto;
import net.thumbtack.onlineshop.service.events.BasketPurchaseEvent;
import net.thumbtack.onlineshop.service.events.ProductPurchaseEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class PurchasesServiceTest {

    private PurchasesService service;

    @Mock
    private ApplicationEventPublisher mockEventPublisher;

    @Mock
    private PurchaseDao mockPurchaseDao;

    @Mock
    private ProductDao mockProductDao;

    @Mock
    private AccountDao mockAccountDao;

    @Mock
    private SessionDao mockSessionDao;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);

        service = new PurchasesService(
                mockSessionDao,
                mockPurchaseDao,
                mockAccountDao,
                mockProductDao,
                mockEventPublisher
        );
    }

    /**
     * Сохранение события о покупке товара
     */
    @Test
    public void testSaveProductPurchase() {

        Product product = new Product();
        product.setPrice(3);
        service.saveProductPurchase(generateAdmin(), product, 3);

        // Просто проверим, что данные методы были вызваны
        verify(mockPurchaseDao).insert(any());
        verify(mockEventPublisher).publishEvent(any(ProductPurchaseEvent.class));
    }

    /**
     * Сохранение события о покупке корзины
     */
    @Test
    public void testSaveBasketPurchase() {

        Map<Product, Integer> products = new HashMap<>();

        Product product = new Product();
        product.setPrice(3);
        products.put(product, 3);

        product = new Product();
        product.setPrice(4);
        products.put(product, 4);

        service.saveBasketPurchase(generateAdmin(), products);

        // Так же просто проверим вызовы
        verify(mockPurchaseDao, times(2)).insert(any());
        verify(mockEventPublisher).publishEvent(any(BasketPurchaseEvent.class));
    }

    /**
     * Получаем историю покупок для множества клиентов
     */
    @Test
    public void testGetPurchasesClientSorted() throws ServiceException {
        setAdmin();

        // Товары который был выкуплен
        Product product = new Product("iphone", 100, 10);
        // Клиент, который купил товар
        Account client = generateClient();

        // Создаём список, который якобы вернётся из БД
        List<Purchase> purchases = Arrays.asList(
                new Purchase(product, client, new Date(), 10, 10),
                new Purchase(product, client, new Date(), 2, 10),
                new Purchase(product, client, new Date(), 3, 10)
        );
        when(mockPurchaseDao.getPurchasesSortedByClients(3, 5))
                .thenReturn(purchases);

        // Получаем результат
        PurchasesDto result = service.getPurchases(
                "token", PurchasesService.Target.CLIENT, 5, 3, null, null);

        assertEquals((int)result.getTotalCount(), 15);
        assertEquals((int)result.getTotalAmount(), 150);
        assertEquals(3, result.getPurchases().size());

        assertEquals("iphone", result.getPurchases().get(0).getProductName());
        assertEquals(10, (int)result.getPurchases().get(0).getCount());
        assertEquals(10, (int)result.getPurchases().get(0).getPrice());
        assertEquals(client.getFullName(), result.getPurchases().get(0).getClientFullName());

        assertEquals("iphone", result.getPurchases().get(1).getProductName());
        assertEquals(2, (int)result.getPurchases().get(1).getCount());
        assertEquals(10, (int)result.getPurchases().get(1).getPrice());
        assertEquals(client.getFullName(), result.getPurchases().get(1).getClientFullName());

        assertEquals("iphone", result.getPurchases().get(2).getProductName());
        assertEquals(3, (int)result.getPurchases().get(2).getCount());
        assertEquals(10, (int)result.getPurchases().get(2).getPrice());
        assertEquals(client.getFullName(), result.getPurchases().get(2).getClientFullName());
    }

    /**
     * Получаем историю покупок для множества товаров
     */
    @Test
    public void testGetPurchasesProductSorted() throws ServiceException {
        setAdmin();

        // Товары который был выкуплен
        Product product = new Product("iphone", 100, 10);
        // Клиент, который купил товар
        Account client = generateClient();

        // Создаём список, который якобы вернётся из БД
        List<Purchase> purchases = Arrays.asList(
                new Purchase(product, client, new Date(), 10, 10),
                new Purchase(product, client, new Date(), 2, 10),
                new Purchase(product, client, new Date(), 3, 10)
        );
        when(mockPurchaseDao.getPurchasesSortedByProducts(3, 5))
                .thenReturn(purchases);

        // Получаем результат
        // Так же проверяем, что если передавать пустой список категорий, то выборки по ним
        // всё равно происходить не будет
        PurchasesDto result = service.getPurchases(
                "token", PurchasesService.Target.PRODUCT, 5, 3, null, Collections.emptyList());

        assertEquals((int)result.getTotalCount(), 15);
        assertEquals((int)result.getTotalAmount(), 150);
        assertEquals(3, result.getPurchases().size());

        assertEquals("iphone", result.getPurchases().get(0).getProductName());
        assertEquals(10, (int)result.getPurchases().get(0).getCount());
        assertEquals(10, (int)result.getPurchases().get(0).getPrice());
        assertEquals(client.getFullName(), result.getPurchases().get(0).getClientFullName());

        assertEquals("iphone", result.getPurchases().get(1).getProductName());
        assertEquals(2, (int)result.getPurchases().get(1).getCount());
        assertEquals(10, (int)result.getPurchases().get(1).getPrice());
        assertEquals(client.getFullName(), result.getPurchases().get(1).getClientFullName());

        assertEquals("iphone", result.getPurchases().get(2).getProductName());
        assertEquals(3, (int)result.getPurchases().get(2).getCount());
        assertEquals(10, (int)result.getPurchases().get(2).getPrice());
        assertEquals(client.getFullName(), result.getPurchases().get(2).getClientFullName());

    }

    /**
     * Получаем историю покупок для множество товаров, которые
     * принадлежат списку указанных категорий
     */
    @Test
    public void testGetPurchasesProductSortedWithCategories() throws ServiceException {
        setAdmin();

        // Товары который был выкуплен
        Product product = new Product("iphone", 100, 10);
        // Клиент, который купил товар
        Account client = generateClient();

        // Создаём список, который якобы вернётся из БД
        List<Purchase> purchases = Arrays.asList(
                new Purchase(product, client, new Date(), 10, 10),
                new Purchase(product, client, new Date(), 2, 10),
                new Purchase(product, client, new Date(), 3, 10)
        );

        // Создаём какой-нибудь список категорий
        List<Long> categories = Arrays.asList(1L, 2L, 3L);

        // Dao должно будет в ответ вернуть множество товаров
        Set<Product> products = new HashSet<>();
        Product temp = new Product();
        temp.setId(1L);
        products.add(temp);

        temp = new Product();
        temp.setId(2L);
        products.add(temp);

        temp = new Product();
        temp.setId(3L);
        products.add(temp);
        when(mockProductDao.getAllWithCategories(categories))
                .thenReturn(products);

        // Теперь можем вернуть историю покупок
        when(mockPurchaseDao.getProductsPurchases(Arrays.asList(2L, 3L, 1L), 3, 5))
                .thenReturn(purchases);

        // Получаем результат
        PurchasesDto result = service.getPurchases(
                "token", PurchasesService.Target.PRODUCT, 5, 3, null, categories);

        assertEquals(15, (int)result.getTotalCount());
        assertEquals(150, (int)result.getTotalAmount());
        assertEquals(3, result.getPurchases().size());

        assertEquals("iphone", result.getPurchases().get(0).getProductName());
        assertEquals(10, (int)result.getPurchases().get(0).getCount());
        assertEquals(10, (int)result.getPurchases().get(0).getPrice());
        assertEquals(client.getFullName(), result.getPurchases().get(0).getClientFullName());

        assertEquals("iphone", result.getPurchases().get(1).getProductName());
        assertEquals(2, (int)result.getPurchases().get(1).getCount());
        assertEquals(10, (int)result.getPurchases().get(1).getPrice());
        assertEquals(client.getFullName(), result.getPurchases().get(1).getClientFullName());

        assertEquals("iphone", result.getPurchases().get(2).getProductName());
        assertEquals(3, (int)result.getPurchases().get(2).getCount());
        assertEquals(10, (int)result.getPurchases().get(2).getPrice());
        assertEquals(client.getFullName(), result.getPurchases().get(2).getClientFullName());

    }

    /**
     * Получаем историю покупок для одного единственного клиента
     */
    @Test
    public void testGetPurchasesSingleClient() throws ServiceException {
        setAdmin();

        // Товары который был выкуплен
        Product product = new Product("iphone", 100, 10);
        // Клиент, который купил товар
        Account client = generateClient();

        // Создаём список, который якобы вернётся из БД
        List<Purchase> purchases = Arrays.asList(
                new Purchase(product, client, new Date(), 10, 10),
                new Purchase(product, client, new Date(), 2, 10),
                new Purchase(product, client, new Date(), 3, 10)
        );

        // Клиент существует
        when(mockAccountDao.exists(3L)).thenReturn(true);

        List<Long> ids = Collections.singletonList(3L);

        // Возвращаем список его покупок
        when(mockPurchaseDao.getClientsPurchases(ids, 3, 5))
                .thenReturn(purchases);

        // Получаем результат
        PurchasesDto result = service.getPurchases(
                "token", PurchasesService.Target.CLIENT, 5, 3, ids, null);

        assertEquals((int)result.getTotalCount(), 15);
        assertEquals((int)result.getTotalAmount(), 150);
        assertEquals(3, result.getPurchases().size());

        assertEquals("iphone", result.getPurchases().get(0).getProductName());
        assertEquals(10, (int)result.getPurchases().get(0).getCount());
        assertEquals(10, (int)result.getPurchases().get(0).getPrice());
        assertEquals(client.getFullName(), result.getPurchases().get(0).getClientFullName());

        assertEquals("iphone", result.getPurchases().get(1).getProductName());
        assertEquals(2, (int)result.getPurchases().get(1).getCount());
        assertEquals(10, (int)result.getPurchases().get(1).getPrice());
        assertEquals(client.getFullName(), result.getPurchases().get(1).getClientFullName());

        assertEquals("iphone", result.getPurchases().get(2).getProductName());
        assertEquals(3, (int)result.getPurchases().get(2).getCount());
        assertEquals(10, (int)result.getPurchases().get(2).getPrice());
        assertEquals(client.getFullName(), result.getPurchases().get(2).getClientFullName());
    }

    /**
     * Получаем историю покупок для одного единственного товара
     */
    @Test
    public void testGetPurchasesSingleProduct() throws ServiceException {
        setAdmin();

        // Товары который был выкуплен
        Product product = new Product("iphone", 100, 10);
        // Клиент, который купил товар
        Account client = generateClient();

        // Создаём список, который якобы вернётся из БД
        List<Purchase> purchases = Arrays.asList(
                new Purchase(product, client, new Date(), 10, 10),
                new Purchase(product, client, new Date(), 2, 10),
                new Purchase(product, client, new Date(), 3, 10)
        );

        // Товар существует
        when(mockProductDao.exists(3L)).thenReturn(true);

        List<Long> ids = Collections.singletonList(3L);

        // Возвращаем список его покупок
        when(mockPurchaseDao.getProductsPurchases(ids, 3, 5))
                .thenReturn(purchases);

        // Получаем результат
        PurchasesDto result = service.getPurchases(
                "token", PurchasesService.Target.PRODUCT, 5, 3, ids, null);

        assertEquals((int)result.getTotalCount(), 15);
        assertEquals((int)result.getTotalAmount(), 150);
        assertEquals(3, result.getPurchases().size());

        assertEquals("iphone", result.getPurchases().get(0).getProductName());
        assertEquals(10, (int)result.getPurchases().get(0).getCount());
        assertEquals(10, (int)result.getPurchases().get(0).getPrice());
        assertEquals(client.getFullName(), result.getPurchases().get(0).getClientFullName());

        assertEquals("iphone", result.getPurchases().get(1).getProductName());
        assertEquals(2, (int)result.getPurchases().get(1).getCount());
        assertEquals(10, (int)result.getPurchases().get(1).getPrice());
        assertEquals(client.getFullName(), result.getPurchases().get(1).getClientFullName());

        assertEquals("iphone", result.getPurchases().get(2).getProductName());
        assertEquals(3, (int)result.getPurchases().get(2).getCount());
        assertEquals(10, (int)result.getPurchases().get(2).getPrice());
        assertEquals(client.getFullName(), result.getPurchases().get(2).getClientFullName());
    }

    /**
     * Нельзя получить историю покупок для несуществующего товара
     */
    @Test(expected = ServiceException.class)
    public void testGetPurchasesSingleProductNotFound() throws ServiceException {
        setAdmin();

        List<Long> ids = Collections.singletonList(3L);
        when(mockProductDao.exists(3L)).thenReturn(false);

        try {
            service.getPurchases("token", PurchasesService.Target.PRODUCT, 5, 3, ids, null);
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.PRODUCT_NOT_FOUND, e.getErrorCode());
            throw e;
        }

    }

    /**
     * Нельзя получить историю покупок для несуществующего клиента
     */
    @Test(expected = ServiceException.class)
    public void testGetPurchasesSingleClientNotFound() throws ServiceException {
        setAdmin();

        List<Long> ids = Collections.singletonList(3L);
        when(mockAccountDao.exists(3L)).thenReturn(false);

        try {
            service.getPurchases("token", PurchasesService.Target.CLIENT, 5, 3, ids, null);
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.USER_NOT_FOUND, e.getErrorCode());
            throw e;
        }

    }

    /**
     * Нельзя получить список покупок с неверной сессией
     */
    @Test(expected = ServiceException.class)
    public void testGetPurchasesNotLogin() throws ServiceException {

        try {
            service.getPurchases("erewr", PurchasesService.Target.CLIENT, 0, 0, null, null);
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
            throw e;
        }
    }

    /**
     * Нельзя получить список покупок, если пользователь
     * не является администратором
     */
    @Test(expected = ServiceException.class)
    public void testGetPurchasesNotAdmin() throws ServiceException {
        setClient();
        try {
            service.getPurchases("token", PurchasesService.Target.CLIENT, 0, 0, null, null);
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_ADMIN, e.getErrorCode());
            throw e;
        }
    }

    private void setAdmin() {
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));
    }

    private void setClient() {
        Account client = generateClient();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));
    }

    private Account generateClient() {
        return AccountFactory.createClient(
                "wer", "wr", "er", "wer", "ser", "ser", "re"
        );
    }

    private Account generateAdmin() {
        return AccountFactory.createAdmin(
                "vadim", "gush", "vadimovich", "coder", "vadim", "Iddqd225"
        );
    }
}
