package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.database.models.AccountFactory;
import net.thumbtack.onlineshop.database.models.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SessionServiceTest {

    private SessionService sessionService;

    @Mock
    private SessionDao mockSessionDao;

    @Mock
    private AccountDao mockAccountDao;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);
        sessionService = new SessionService(mockAccountDao, mockSessionDao);
    }

    @Test
    public void testLogin() throws ServiceException {
        Account account = AccountFactory.createAdmin(
                "werew", "werwr", "werwe", "werew", "wer"
        );
        when(mockAccountDao.get("login", "password")).thenReturn(account);

        String token = sessionService.login("login", "password");
        verify(mockSessionDao).insert(any());
        assertNotNull(token);
    }

    @Test(expected = ServiceException.class)
    public void testLoginWithWrongCred() throws ServiceException {

        when(mockAccountDao.get("login", "password")).thenReturn(null);

        try {
            sessionService.login("login", "password");
        } catch (ServiceException e) {
            verify(mockSessionDao, never()).insert(any());
            assertEquals(ServiceException.ErrorCode.USER_NOT_FOUND, e.getErrorCode());
            throw e;
        }
    }

    @Test
    public void testLogout() {
        Session session = new Session();
        when(mockSessionDao.get("token")).thenReturn(session);

        sessionService.logout("token");

        verify(mockSessionDao).delete(session);
    }

    @Test
    public void testLogoutWrongSession() {
        when(mockSessionDao.get("token")).thenReturn(null);

        sessionService.logout("token");

        verify(mockSessionDao, never()).delete(any());
    }

    @Test
    public void testGetAccount() throws ServiceException {
        Account account = AccountFactory.createAdmin(
                "rwer", "werew", "werew", "werw", "werew"
        );
        when(mockSessionDao.get("token")).thenReturn(new Session("token", account));

        Account result = sessionService.getAccount("token");
        assertEquals(result, account);
    }

    @Test(expected = ServiceException.class)
    public void testGetAccountWrongSession() throws ServiceException {
        when(mockSessionDao.get("token")).thenReturn(null);

        try {
            sessionService.getAccount("token");
        } catch (ServiceException e) {
            assertEquals(ServiceException.ErrorCode.NOT_LOGIN, e.getErrorCode());
            throw e;
        }
    }

}
