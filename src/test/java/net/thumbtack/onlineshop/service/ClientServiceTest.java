package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.database.models.AccountFactory;
import net.thumbtack.onlineshop.database.models.Session;
import net.thumbtack.onlineshop.dto.ClientDto;
import net.thumbtack.onlineshop.dto.ClientEditDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);
        clientService = new ClientService(mockAccountDao, mockSessionDao);
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
            verify(mockAccountDao, never()).insert(any());
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

        verify(mockAccountDao).update(any());

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
            verify(mockAccountDao, never()).update(any());
            assertEquals(ServiceException.ErrorCode.WRONG_PASSWORD, e.getErrorCode());
            throw e;
        }

    }

    private Account generateClient() {
        return AccountFactory.createClient(
                "234324", "234324", "wereww1", "werewr1",
                "wrewf3", "werwer235", "werw23", "ewr23423"
        );
    }
}
