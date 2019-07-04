package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.BasketDao;
import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.*;
import net.thumbtack.onlineshop.dto.BuyProductDto;
import net.thumbtack.onlineshop.dto.ClientDto;
import net.thumbtack.onlineshop.dto.ClientEditDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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

    @Test
    public void testRegister() throws ServiceException {

        // Проверяем регистрацию как обычно

        Account client = generateClient();

        when(mockAccountDao.exists(client.getLogin())).thenReturn(false);

        Account result = clientService.register(new ClientDto(client));

        verify(mockAccountDao).insert(any());

        assertEquals(client.getFirstName(), result.getFirstName());
        assertEquals(client.getSecondName(), result.getSecondName());
        assertEquals(client.getThirdName(), result.getThirdName());
        assertEquals(client.getDeposit(), result.getDeposit());
        assertEquals(client.getEmail(), result.getEmail());
        assertEquals(client.getPostAddress(), result.getPostAddress());
        assertEquals(client.getPhone(), result.getPhone());
        assertFalse(result.isAdmin());

        // Проверяем регистрацию без отчества

        client = generateClient();
        client.setThirdName(null);

        result = clientService.register(new ClientDto(client));

        verify(mockAccountDao, times(2)).insert(any());

        assertNull(result.getThirdName());
    }

    @Test(expected = ServiceException.class)
    public void testRegisterWithSameLogin() throws ServiceException {
        Account client = generateClient();

        when(mockAccountDao.exists(client.getLogin())).thenReturn(true);

        try {
            clientService.register(new ClientDto(client));

        } catch (ServiceException e) {
            verify(mockAccountDao, never()).insert(client);
            assertEquals(ServiceException.ErrorCode.LOGIN_ALREADY_IN_USE, e.getErrorCode());
            throw e;
        }
    }

    @Test
    public void testEdit() throws ServiceException {

        Account client = generateClient();

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        ClientEditDto edited = new ClientEditDto(
                "new name", "new last name", "new patro",
                "new email", "new address", "new phone", client.getPassword(),
                "new password"
        );

        Account result = clientService.edit("token", edited);

        verify(mockAccountDao).update(client);

        assertEquals(edited.getFirstName(), result.getFirstName());
        assertEquals(edited.getLastName(), result.getSecondName());
        assertEquals(edited.getPatronymic(), result.getThirdName());
        assertEquals(edited.getAddress(), result.getPostAddress());
        assertEquals(edited.getEmail(), result.getEmail());
        assertEquals(edited.getNewPassword(), result.getPassword());
        assertEquals(edited.getPhone(), result.getPhone());
        assertFalse(result.isAdmin());

    }

    @Test(expected = ServiceException.class)
    public void testEditWrongPassword() throws ServiceException {

        Account client = generateClient();

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        ClientEditDto edited = new ClientEditDto(
                "new name", "new last name", "new patro",
                "new email", "new address", "new phone", "wrong",
                "new password"
        );

        try {
            clientService.edit("token", edited);

        } catch (ServiceException e) {
            verify(mockAccountDao, never()).update(client);
            assertEquals(ServiceException.ErrorCode.WRONG_PASSWORD, e.getErrorCode());
            throw e;
        }
    }

    @Test()
    public void testPutDeposit() throws ServiceException {

        Account client = generateClient();
        client.setDeposit(9);

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        Account result = clientService.putDeposit("token", 12);

        verify(mockAccountDao).update(client);

        assertEquals(21, (int)result.getDeposit());

    }

    @Test()
    public void testGetDeposit() throws ServiceException {

        Account client = generateClient();
        client.setDeposit(9);

        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        Account result = clientService.getDeposit("token");

        assertEquals((int)client.getDeposit(), (int)result.getDeposit());

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

        BuyProductDto request = new BuyProductDto(0, "product", 10, 5);
        BuyProductDto result = clientService.buyProduct("token", request);

        // Весь товар должен просто удалится
        verify(mockProductDao).delete(product);
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

        BuyProductDto request = new BuyProductDto(0, "product", 10, 2);
        BuyProductDto result = clientService.buyProduct("token", request);

        // Количество товара просто должно изменится
        verify(mockProductDao).update(product);
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
            BuyProductDto request = new BuyProductDto(1, "product", 10, 2);
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
            BuyProductDto request = new BuyProductDto(1, "product", 10, 2);
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
            BuyProductDto request = new BuyProductDto(0, "product2", 10, 2);
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
            BuyProductDto request = new BuyProductDto(0, "product", 9, 2);
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
            BuyProductDto request = new BuyProductDto(0, "product2", 10, 2);
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
            BuyProductDto request = new BuyProductDto(0, "product", 12, 2);
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
            BuyProductDto request = new BuyProductDto(0, "product", 10, 6);
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
            BuyProductDto request = new BuyProductDto(0, "product", 10, 5);
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

        when(mockProductDao.get(0)).thenReturn(product);

        // По требованиям он может добавить сколько угодно товара
        // на какую угодно сумму вне зависимости от его депозита
        BuyProductDto request = new BuyProductDto(0, "product", 10, 50);

        Basket basket = new Basket(client, product, request.getCount());
        when(mockBasketDao.get(client)).thenReturn(Arrays.asList(basket));

        List<Basket> result = clientService.addToBasket("token", request);

        // Должна была быть вставка одно единственного товара в корзину
        verify(mockBasketDao).insert(any());

        // Без каких-либо изменений на стороне клиентов или склада
        verify(mockAccountDao, never()).update(any());
        verify(mockProductDao, never()).update(any());
        verify(mockProductDao, never()).delete(any());

        assertEquals(1, result.size());

        Product fromBasket = result.get(0).getProduct();
        assertEquals(request.getName(), fromBasket.getName());
        assertEquals(request.getPrice(), (int)fromBasket.getPrice());
        assertEquals(request.getCount(), (int)result.get(0).getCount());

        assertEquals(client, result.get(0).getAccount());
    }



    private Account generateClient() {
        return AccountFactory.createClient(
                "234324", "234324", "wereww1", "werewr1",
                "wrewf3", "werwer235", "werw23", "ewr23423"
        );
    }
}
