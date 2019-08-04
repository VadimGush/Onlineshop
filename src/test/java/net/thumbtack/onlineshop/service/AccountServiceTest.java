package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.domain.dao.AccountDao;
import net.thumbtack.onlineshop.domain.dao.SessionDao;
import net.thumbtack.onlineshop.domain.models.Account;
import net.thumbtack.onlineshop.domain.models.AccountFactory;
import net.thumbtack.onlineshop.domain.models.Session;
import net.thumbtack.onlineshop.dto.AccountDto;
import net.thumbtack.onlineshop.dto.AdminDto;
import net.thumbtack.onlineshop.dto.ClientDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static net.thumbtack.onlineshop.utils.TestUtils.createAdminEditDto;
import static net.thumbtack.onlineshop.utils.TestUtils.createClientEditDto;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    private AccountService accountService;

    @Mock
    private AccountDao mockAccountDao;

    @Mock
    private SessionDao mockSessionDao;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);
        accountService = new AccountService(mockAccountDao, mockSessionDao);
    }

    /**
     * Регистрация администратора
     */
    @Test
    public void testRegistration() throws ServiceException {
        // Создаём администратора
        Account admin = generateAdmin();
        // Логин не занят
        when(mockAccountDao.exists(admin.getLogin())).thenReturn(false);

        // TODO: Дописать тесты для сессии
        Account result = accountService.register(new AdminDto(admin)).getFirst();

        verify(mockAccountDao).insert(any());

        assertTrue(result.isAdmin());
        assertEquals(admin.getFirstName(), result.getFirstName());
        assertEquals(admin.getLastName(), result.getLastName());
        assertEquals(admin.getPatronymic(), result.getPatronymic());
        assertEquals(admin.getPosition(), result.getPosition());
        assertEquals(admin.getLogin(), result.getLogin());
        assertEquals(admin.getPassword(), result.getPassword());

    }

    /**
     * Регистрация администратора без отчества
     */
    @Test
    public void testRegistrationWithoutPatronymic() throws ServiceException {
        // Создаём администратора
        Account admin = generateAdmin();
        admin.setPatronymic(null);
        // Логин не занят
        when(mockAccountDao.exists(admin.getLogin())).thenReturn(false);

        // TODO: Дописать тесты для сессии
        Account result = accountService.register(new AdminDto(admin)).getFirst();

        verify(mockAccountDao).insert(any());

        assertNull(result.getPatronymic());
    }

    /**
     * Нельзя создать админа с занятым логином
     */
    @Test(expected = ServiceException.class)
    public void testRegisterWithSameLogin() throws ServiceException {
        Account admin =  generateAdmin();

        try {
            // RequiredLogin already in use
            when(mockAccountDao.exists("vadim")).thenReturn(true);

            accountService.register(new AdminDto(
                    admin.getFirstName(), admin.getLastName(), admin.getPatronymic(),
                    admin.getPosition(), admin.getLogin(), admin.getPassword()
            ));

        } catch (ServiceException e) {
            // Записиси пользователя не произошло
            verify(mockAccountDao, never()).insert(admin);

            assertEquals(ServiceException.ErrorCode.LOGIN_ALREADY_IN_USE, e.getErrorCode());
            throw e;
        }
    }

    /**
     * Редактирование профиля администратора
     */
    @Test
    public void testEdit() throws ServiceException {
        // Создаём админа
        Account admin = AccountFactory.createAdmin(
                "werewrwe", "ewrwe", "werew", "erer2", "vadim", "23"
        );
        admin.setId(3L);
        when(mockAccountDao.isPasswordMatch(3L, "23")).thenReturn(true);
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

        // Изменяем аккаунт
        AccountDto result = accountService.edit("token", createAdminEditDto(
                "name", "lastName", "patro", "pos", "23", "33"
        ));

        verify(mockAccountDao).update(any());

        assertEquals("name", result.getFirstName());
        assertEquals("lastName", result.getLastName());
        assertEquals("patro", result.getPatronymic());
        assertEquals("pos", result.getPosition());
    }

    /**
     * Нельзя редактировать с неверно указанным старым паролем
     */
    @Test(expected = ServiceException.class)
    public void testEditWithWrongPassword() throws ServiceException {

        // Создаём администратора
        Account admin = AccountFactory.createAdmin(
                "werewrwe", "ewrwe", "werew", "erer2", "vadim", "23"
        );
        admin.setId(3L);
        when(mockAccountDao.isPasswordMatch(3L, "43")).thenReturn(false);
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

        try {
            // Редактируем аккаунт
            accountService.edit("token", createAdminEditDto(
                    "name", "lastName", "patro", "pos", "43", "33"
            ));

            verify(mockAccountDao, never()).update(any());
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.WRONG_PASSWORD, e.getErrorCode());
            throw e;
        }
    }

    /**
     * Получаем список всех клиентов
     */
    @Test
    public void testGetAll() throws ServiceException {
        // Создаём клиентов
        List<Account> clients = new ArrayList<>();
        clients.add(generateClient());
        clients.add(generateClient());

        when(mockAccountDao.getClients()).thenReturn(clients);
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

        // Получаем список
        List<AccountDto> result = accountService.getAll("token");
        assertEquals(clients.size(), result.size());

        // Каждый клиент должен содержать поле userType
        assertEquals("client", result.get(0).getUserType());
        assertEquals("client", result.get(1).getUserType());
        assertNull(result.get(0).getDeposit());
        assertNull(result.get(1).getDeposit());

        verify(mockAccountDao).getClients();
    }

    /**
     * Нельзя редактировать аккаунт админа без логина
     */
    @Test(expected = ServiceException.class)
    public void testNotLoginEdit() throws ServiceException {
        when(mockSessionDao.get("token")).thenReturn(null);

        try {
            accountService.edit("token", new AdminDto());

        } catch (ServiceException e) {
            verify(mockAccountDao, never()).update(any());

            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
            throw e;
        }
    }

    /**
     * Нельзя редактировать аккаунт админа от имени клиента
     */
    @Test(expected = ServiceException.class)
    public void testNotAdminEdit() throws ServiceException {
        when(mockSessionDao.get("token")).thenReturn(new Session("token", generateClient()));

        try {
            accountService.edit("token", new AdminDto());

        } catch (ServiceException e) {
            verify(mockAccountDao, never()).update(any());

            assertEquals(ServiceException.ErrorCode.NOT_ADMIN, e.getErrorCode());
            throw e;
        }
    }

    /**
     * Нельзя получить список всех клиентов без логина
     */
    @Test(expected = ServiceException.class)
    public void testNotLoginGetAll() throws ServiceException {
        when(mockSessionDao.get("token")).thenReturn(null);

        try {
            accountService.getAll("token");

        } catch (ServiceException e) {
            verify(mockAccountDao, never()).getClients();

            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
            throw e;
        }
    }

    /**
     * Нельзя получить список всех клиентов от имени клиента
     */
    @Test(expected = ServiceException.class)
    public void testNotAdminGetAll() throws ServiceException {
        when(mockSessionDao.get("token")).thenReturn(new Session("token", generateClient()));

        try {
            accountService.getAll("token");

        } catch (ServiceException e) {
            verify(mockAccountDao, never()).update(any());

            assertEquals(ServiceException.ErrorCode.NOT_ADMIN, e.getErrorCode());
            throw e;
        }
    }

    /**
     * Регистрация клиента
     */
    @Test
    public void testClientRegister() throws ServiceException {

        // Создаём клиента
        Account client = generateClient();
        // Логин свободен
        when(mockAccountDao.exists(client.getLogin())).thenReturn(false);

        // TODO: Дописать тесты для сессии
        // Регаем
        Account result = accountService.register(new ClientDto(client)).getFirst();

        verify(mockAccountDao).insert(any());

        assertEquals(client.getFirstName(), result.getFirstName());
        assertEquals(client.getLastName(), result.getLastName());
        assertEquals(client.getPatronymic(), result.getPatronymic());
        assertEquals(client.getDeposit(), result.getDeposit());
        assertEquals(client.getEmail(), result.getEmail());
        assertEquals(client.getAddress(), result.getAddress());
        assertEquals(client.getPhone(), result.getPhone());
        assertFalse(result.isAdmin());

    }

    /**
     * Регистрация клиента без отчества
     */
    @Test
    public void testClientRegisterWithoutPatronymic() throws ServiceException {
        // Создаём клиента
        Account client = generateClient();
        client.setPatronymic(null);
        // Логин свободен
        when(mockAccountDao.exists(client.getLogin())).thenReturn(false);

        // TODO: Дописать тесты для сессии
        // Регаем
        Account result = accountService.register(new ClientDto(client)).getFirst();

        verify(mockAccountDao).insert(any());

        assertNull(result.getPatronymic());

    }

    /**
     * Нельзя зарегать клиента с занятым логином
     */
    @Test(expected = ServiceException.class)
    public void testClientRegisterWithSameLogin() throws ServiceException {
        Account client = generateClient();

        when(mockAccountDao.exists(client.getLogin())).thenReturn(true);

        try {
            accountService.register(new ClientDto(client));

        } catch (ServiceException e) {
            verify(mockAccountDao, never()).insert(client);
            assertEquals(ServiceException.ErrorCode.LOGIN_ALREADY_IN_USE, e.getErrorCode());
            throw e;
        }
    }

    /**
     * Редактирование аккаунта клиента
     */
    @Test
    public void testClientEdit() throws ServiceException {
        Account client = generateClient();
        client.setId(3L);
        when(mockAccountDao.isPasswordMatch(3L, client.getPassword())).thenReturn(true);
        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        ClientDto edited = createClientEditDto(
                "new name", "new last name", "new patro",
                "new email", "new address", "new phone", client.getPassword(),
                "new password"
        );

        AccountDto result = accountService.edit("token", edited);

        verify(mockAccountDao).update(client);

        assertEquals(edited.getFirstName(), result.getFirstName());
        assertEquals(edited.getLastName(), result.getLastName());
        assertEquals(edited.getPatronymic(), result.getPatronymic());
        assertEquals(edited.getAddress(), result.getAddress());
        assertEquals(edited.getEmail(), result.getEmail());
        assertEquals(edited.getPhone(), result.getPhone());
    }

    /**
     * Нельзя редактировать аккаунт клиента с неверным старым паролем
     */
    @Test(expected = ServiceException.class)
    public void testClientEditWrongPassword() throws ServiceException {

        Account client = generateClient();
        client.setId(3L);
        when(mockAccountDao.isPasswordMatch(3L, "wrong")).thenReturn(false);
        when(mockSessionDao.get("token")).thenReturn(new Session("token", client));

        ClientDto edited = createClientEditDto(
                "new name", "new last name", "new patro",
                "new email", "new address", "new phone", "wrong",
                "new password"
        );

        try {
            accountService.edit("token", edited);

        } catch (ServiceException e) {
            verify(mockAccountDao, never()).update(client);
            assertEquals(ServiceException.ErrorCode.WRONG_PASSWORD, e.getErrorCode());
            throw e;
        }
    }

    /**
     * Вход в аккаунт
     */
    @Test
    public void testLogin() throws ServiceException {
        Account account = AccountFactory.createAdmin(
                "werew", "werwr", "werwe", "werew", "wer"
        );
        when(mockAccountDao.get("login", "password")).thenReturn(account);

        String token = accountService.login("login", "password");
        verify(mockSessionDao).insert(any());
        assertNotNull(token);
    }

    /**
     * Нельзя залогиниться с неверным паролем или логином
     */
    @Test(expected = ServiceException.class)
    public void testClientLoginWithWrongCred() throws ServiceException {

        when(mockAccountDao.get("login", "password")).thenReturn(null);

        try {
            accountService.login("login", "password");
        } catch (ServiceException e) {
            verify(mockSessionDao, never()).insert(any());
            assertEquals(ServiceException.ErrorCode.USER_NOT_FOUND, e.getErrorCode());
            throw e;
        }
    }

    /**
     * Выход из аккаунта
     */
    @Test
    public void testLogout() {
        Session session = new Session();
        when(mockSessionDao.get("token")).thenReturn(session);

        accountService.logout("token");

        verify(mockSessionDao).delete(session);
    }

    /**
     * Выход из аккаунта работает даже если не было логина
     * или сессия неверная
     */
    @Test
    public void testLogoutWrongSession() {
        when(mockSessionDao.get("token")).thenReturn(null);

        accountService.logout("token");

        verify(mockSessionDao, never()).delete(any());
    }

    /**
     * Получение информации об аккаунте
     */
    @Test
    public void testGetAccount() throws ServiceException {
        Account account = AccountFactory.createAdmin(
                "rwer", "werew", "werew", "werw", "werew"
        );
        when(mockSessionDao.get("token")).thenReturn(new Session("token", account));

        AccountDto result = accountService.get("token");
        // Логин и пароль не возвращаются
        assertEquals(account.getFirstName(), result.getFirstName());
        assertEquals(account.getLastName(), result.getLastName());
        assertEquals(account.getPosition(), result.getPosition());
    }

    /**
     * Нельзя получить информацию об аккаунте без логина
     */
    @Test(expected = ServiceException.class)
    public void testGetAccountWrongSession() throws ServiceException {
        when(mockSessionDao.get("token")).thenReturn(null);

        try {
            accountService.get("token");
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
            throw e;
        }
    }

    private Account generateClient() {
        return AccountFactory.createClient(
                "rewrw", "sder", "werew", "ewrwe", "wrwe", "werwe", "werw"
        );
    }

    private Account generateAdmin() {
        return AccountFactory.createAdmin(
                "vadim", "gush", "vadimovich", "coder", "vadim", "Iddqd225"
        );
    }


}
