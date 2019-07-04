package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.database.models.AccountFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class AdminServiceTest {

    private AdminService adminService;

    @Mock
    private AccountDao accountDao;

    @Mock
    private SessionDao sessionDao;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);
        adminService = new AdminService(accountDao, sessionDao);
    }

    @Test
    public void testRegistration() throws ServiceException {

        Account admin = AccountFactory.createAdmin(
                "vadim", "gush", "vadimovich", "coder", "vadim", "Iddqd225"
        );

        // Предполагаем что логин свободный
        when(accountDao.exists(admin.getLogin())).thenReturn(false);

        // Регистрируем администратора
        Account result = adminService.register(admin);

        // Должная быть запись в базу данных
        verify(accountDao).insert(admin);

        assertTrue(result.isAdmin());
        assertEquals(admin.getFirstName(), result.getFirstName());
        assertEquals(admin.getSecondName(), result.getSecondName());
        assertEquals(admin.getThirdName(), result.getThirdName());
        assertEquals(admin.getProfession(), result.getProfession());
        assertEquals(admin.getLogin(), result.getLogin());
        assertEquals(admin.getPassword(), result.getPassword());

        // Провряем что можно без отчества
        admin = AccountFactory.createAdmin(
                "vadim", "gush", "coder", "vadim2", "Iddqd225"
        );
        result = adminService.register(admin);

        // Была запись в БД
        verify(accountDao).insert(admin);

        // У возвращаемого объекта такое же отчество
        assertNull(result.getThirdName());
    }

    @Test(expected = ServiceException.class)
    public void testRegisterWithSameLogin() throws ServiceException {
        // Проверяем что администратора с тем же логином создать нельзя

        Account admin = AccountFactory.createAdmin(
                "werewrwe", "ewrwe", "werew", "erer2", "vadim", "23423j"
        );

        try {
            // Login already in use
            when(accountDao.exists("vadim")).thenReturn(true);

            adminService.register(admin);

        } catch (ServiceException e) {

            // Записиси пользователя не произошло
            verify(accountDao, never()).insert(admin);

            assertEquals(ServiceException.ErrorCode.LOGIN_ALREADY_IN_USE, e.getErrorCode());
            throw e;
        }
    }

    @Test
    public void testLogin() {
        // Регистрируем администратора

        // Выходим из его сессии

        // Проверяем что запрос на получение клентов не работает

        // Логинимся

        // Проверяем что запрос на получение клиентов работает
    }

    @Test
    public void testGetAllClients() {
        // Регистрируем несколько пользователей

        // Регистрируем одного администратора

        // Получаем клиентов и проверяем списки
    }

    @Test
    public void testAccountInfo() {
        // Регистрируем администратора

        // Получаем информацию об администраторе
    }

    @Test
    public void testWrongSession() {
        // Проверяем что не один запрос из AdminService не выполнится с
        // Неверным session
    }

}
