package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.controller.validation.ValidationException;
import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.dto.*;
import net.thumbtack.onlineshop.service.AccountService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountControllerTest {

    private AccountController accountController;

    @Mock
    private AccountService mockAccountService;

    @Mock
    private BindingResult mockResult;

    @Mock
    private HttpServletResponse mockResponse;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);

        accountController = new AccountController(mockAccountService);
    }

    @Test
    public void testRegisterClient() throws Exception {

        ClientDto client = new ClientDto();
        client.setLogin("LOGIN");

        Account temp = new Account();
        temp.setLogin("login");
        temp.setPassword("password");
        temp.setFirstName("Vadim");
        temp.setLastName("Gush");

        when(mockResult.hasErrors()).thenReturn(false);
        when(mockAccountService.register(client)).thenReturn(temp);

        AccountDto result = accountController.registerClient(client, mockResult, mockResponse);

        assertEquals("login", client.getLogin());
        verify(mockAccountService).register(client);
        verify(mockResponse).addCookie(any());
        verify(mockAccountService).login(temp.getLogin(), temp.getPassword());

        assertEquals(temp.getFirstName(), result.getFirstName());
        assertEquals(temp.getLastName(), result.getLastName());
    }

    @Test(expected = ValidationException.class)
    public void testRegisterClientValidation() throws Exception {

        ClientDto client = new ClientDto();
        client.setLogin("LOGIN");
        when(mockResult.hasErrors()).thenReturn(true);

        try {
            accountController.registerClient(client, mockResult, mockResponse);
        } catch (ValidationException e) {
            verify(mockAccountService, never()).register(any(ClientDto.class));
            throw e;
        }

    }

    @Test
    public void testRegisterAdmin() throws Exception {

        AdminDto admin = new AdminDto();
        admin.setLogin("LOGIN");

        Account temp = new Account();
        temp.setLogin("login");
        temp.setPassword("password");
        temp.setFirstName("Vadim");
        temp.setLastName("Gush");

        when(mockResult.hasErrors()).thenReturn(false);
        when(mockAccountService.register(admin)).thenReturn(temp);

        AccountDto result = accountController.registerAdmin(admin, mockResult, mockResponse);

        assertEquals("login", admin.getLogin());
        verify(mockAccountService).register(admin);
        verify(mockResponse).addCookie(any());
        verify(mockAccountService).login(temp.getLogin(), temp.getPassword());

        assertEquals(temp.getFirstName(), result.getFirstName());
        assertEquals(temp.getLastName(), result.getLastName());
    }

    @Test(expected = ValidationException.class)
    public void testRegisterAdminValidation() throws Exception {

        AdminDto admin = new AdminDto();
        admin.setLogin("LOGIN");
        when(mockResult.hasErrors()).thenReturn(true);

        try {
            accountController.registerAdmin(admin, mockResult, mockResponse);
        } catch (ValidationException e) {
            verify(mockAccountService, never()).register(any(AdminDto.class));
            throw e;
        }

    }

    @Test
    public void testEditClient() throws Exception {

        ClientEditDto client = new ClientEditDto();
        AccountDto expected = new AccountDto();

        when(mockResult.hasErrors()).thenReturn(false);
        when(mockAccountService.edit("token", client)).thenReturn(expected);

        AccountDto result = accountController.editClient("token", client, mockResult);

        verify(mockAccountService).edit("token", client);
        assertEquals(expected, result);
    }

    @Test(expected = ValidationException.class)
    public void testEditClientValidation() throws Exception {

        ClientEditDto client = new ClientEditDto();
        when(mockResult.hasErrors()).thenReturn(true);

        try {
            accountController.editClient("token", client, mockResult);
        } catch (ValidationException e) {
            verify(mockAccountService, never()).edit(any(), any(ClientEditDto.class));
            throw e;
        }

    }

    @Test
    public void testEditAdmin() throws Exception {

        AdminEditDto admin = new AdminEditDto();
        AccountDto expected = new AccountDto();

        when(mockResult.hasErrors()).thenReturn(false);
        when(mockAccountService.edit("token", admin)).thenReturn(expected);

        AccountDto result = accountController.editAdmin("token", admin, mockResult);

        verify(mockAccountService).edit("token", admin);
        assertEquals(expected, result);
    }

    @Test(expected = ValidationException.class)
    public void testEditAdminValidation() throws Exception {

        AdminEditDto admin = new AdminEditDto();
        when(mockResult.hasErrors()).thenReturn(true);

        try {
            accountController.editAdmin("token", admin, mockResult);
        } catch (ValidationException e) {
            verify(mockAccountService, never()).edit(any(), any(AdminEditDto.class));
            throw e;
        }

    }

    @Test
    public void testGetClients() throws Exception {

        List<AccountDto> expected = new ArrayList<>();
        when(mockAccountService.getAll("token")).thenReturn(expected);

        List<AccountDto> result = accountController.getClients("token");

        verify(mockAccountService).getAll("token");
        assertEquals(expected, result);
    }

    @Test
    public void testGetAccount() throws Exception {

        AccountDto expected = new AccountDto();
        when(mockAccountService.get("token")).thenReturn(expected);

        AccountDto result = accountController.getAccount("token");

        verify(mockAccountService).get("token");
        assertEquals(expected, result);

    }

    @Test
    public void testLogin() throws Exception {

        AccountDto expected = new AccountDto();
        expected.setFirstName("vadim");
        expected.setLastName("gush");

        LoginDto account = new LoginDto();
        account.setLogin("LOGIN");
        account.setPassword("pass");
        when(mockResult.hasErrors()).thenReturn(false);
        when(mockAccountService.login("login", "pass")).thenReturn("token");
        when(mockAccountService.get("token")).thenReturn(expected);

        AccountDto result = accountController.login(account, mockResult, mockResponse);

        verify(mockAccountService).login("login", "pass");
        verify(mockResponse).addCookie(any());
        assertEquals("login", account.getLogin());

        assertEquals(expected.getFirstName(), result.getFirstName());
        assertEquals(expected.getLastName(), result.getLastName());

    }

    @Test(expected = ValidationException.class)
    public void testLoginValidation() throws Exception {

        when(mockResult.hasErrors()).thenReturn(true);
        LoginDto account = new LoginDto();

        try {
            accountController.login(account, mockResult, mockResponse);
        } catch (ValidationException e) {
            verify(mockAccountService, never()).login(any(), any());
            verify(mockResponse, never()).addCookie(any());
            throw e;
        }

    }

    @Test
    public void testLogout() throws Exception {

        accountController.logout("token");
        verify(mockAccountService).logout("token");
    }


}
