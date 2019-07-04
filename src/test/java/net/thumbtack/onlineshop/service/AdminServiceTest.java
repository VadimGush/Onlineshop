package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.database.models.AccountFactory;
import net.thumbtack.onlineshop.database.models.Session;
import net.thumbtack.onlineshop.dto.AdminDto;
import net.thumbtack.onlineshop.dto.AdminEditDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class AdminServiceTest {

    private AdminService adminService;

    @Mock
    private AccountDao mockAccountDao;

    @Mock
    private SessionDao mockSessionDao;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);
        adminService = new AdminService(mockAccountDao, mockSessionDao);
    }

    @Test
    public void testRegistration() throws ServiceException {

        // Проверяем регистрацию администратора
        Account admin = generateAdmin();

        when(mockAccountDao.exists(admin.getLogin())).thenReturn(false);

        Account result = adminService.register(new AdminDto(admin));

        verify(mockAccountDao).insert(any());

        assertTrue(result.isAdmin());
        assertEquals(admin.getFirstName(), result.getFirstName());
        assertEquals(admin.getSecondName(), result.getSecondName());
        assertEquals(admin.getThirdName(), result.getThirdName());
        assertEquals(admin.getProfession(), result.getProfession());
        assertEquals(admin.getLogin(), result.getLogin());
        assertEquals(admin.getPassword(), result.getPassword());

        // Проверяем регистрацию без отчества

        admin = generateAdmin();
        admin.setThirdName(null);
        result = adminService.register(new AdminDto(admin));

        verify(mockAccountDao, times(2)).insert(any());

        assertNull(result.getThirdName());
    }

    @Test(expected = ServiceException.class)
    public void testRegisterWithSameLogin() throws ServiceException {
        // Проверяем что администратора с тем же логином создать нельзя
        Account admin =  generateAdmin();

        try {
            // Login already in use
            when(mockAccountDao.exists("vadim")).thenReturn(true);

            adminService.register(new AdminDto(
                    admin.getFirstName(), admin.getSecondName(), admin.getThirdName(),
                    admin.getProfession(), admin.getLogin(), admin.getPassword()
            ));

        } catch (ServiceException e) {
            // Записиси пользователя не произошло
            verify(mockAccountDao, never()).insert(admin);

            assertEquals(ServiceException.ErrorCode.LOGIN_ALREADY_IN_USE, e.getErrorCode());
            throw e;
        }
    }

    @Test
    public void testEdit() throws ServiceException {
        Account admin = AccountFactory.createAdmin(
                "werewrwe", "ewrwe", "werew", "erer2", "vadim", "23"
        );
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

        Account result = adminService.edit("token", new AdminEditDto(
                "name", "lastName", "patro", "pos", "23", "33"
        ));

        verify(mockAccountDao).update(any());

        assertEquals("name", result.getFirstName());
        assertEquals("lastName", result.getSecondName());
        assertEquals("patro", result.getThirdName());
        assertEquals("pos", result.getProfession());
        assertEquals("33", result.getPassword());
    }

    @Test(expected = ServiceException.class)
    public void testEditWithWrongPassword() throws ServiceException {

        Account admin = AccountFactory.createAdmin(
                "werewrwe", "ewrwe", "werew", "erer2", "vadim", "23"
        );
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

        try {
            adminService.edit("token", new AdminEditDto(
                    "name", "lastName", "patro", "pos", "43", "33"
            ));

            verify(mockAccountDao, never()).update(any());
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.WRONG_PASSWORD, e.getErrorCode());
            throw e;
        }
    }

    @Test
    public void testGetAll() throws ServiceException {
        List<Account> clients = new ArrayList<>();
        clients.add(generateClient());
        clients.add(generateClient());

        when(mockAccountDao.getClients()).thenReturn(clients);
        Account admin = generateAdmin();
        when(mockSessionDao.get("token")).thenReturn(new Session("token", admin));

        List<Account> result = adminService.getAll("token");
        assertEquals(clients.size(), result.size());

        verify(mockAccountDao).getClients();
    }

    @Test(expected = ServiceException.class)
    public void testNotLoginEdit() throws ServiceException {
        when(mockSessionDao.get("token")).thenReturn(null);

        try {
            adminService.edit("token", new AdminEditDto());

        } catch (ServiceException e) {
            verify(mockAccountDao, never()).update(any());

            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
            throw e;
        }
    }

    @Test(expected = ServiceException.class)
    public void testNotAdminEdit() throws ServiceException {
        when(mockSessionDao.get("token")).thenReturn(new Session("token", generateClient()));

        try {
            adminService.edit("token", new AdminEditDto());

        } catch (ServiceException e) {
            verify(mockAccountDao, never()).update(any());

            assertEquals(ServiceException.ErrorCode.NOT_ADMIN, e.getErrorCode());
            throw e;
        }
    }

    @Test(expected = ServiceException.class)
    public void testNotLoginGetAll() throws ServiceException {
        when(mockSessionDao.get("token")).thenReturn(null);

        try {
            adminService.getAll("token");

        } catch (ServiceException e) {
            verify(mockAccountDao, never()).getClients();

            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
            throw e;
        }
    }

    @Test(expected = ServiceException.class)
    public void testNotAdminGetAll() throws ServiceException {
        when(mockSessionDao.get("token")).thenReturn(new Session("token", generateClient()));

        try {
            adminService.getAll("token");

        } catch (ServiceException e) {
            verify(mockAccountDao, never()).update(any());

            assertEquals(ServiceException.ErrorCode.NOT_ADMIN, e.getErrorCode());
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
