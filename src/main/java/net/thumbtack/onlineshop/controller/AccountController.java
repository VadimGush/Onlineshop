package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.controller.validation.ValidationException;
import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.dto.*;
import net.thumbtack.onlineshop.service.AccountService;
import net.thumbtack.onlineshop.service.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api")
public class AccountController {


    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("clients")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto registerClient(
            @RequestBody @Valid ClientDto client,
            BindingResult result,
            HttpServletResponse response) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        client.setLogin(client.getLogin().toLowerCase());

        return setCookieAndReturn(accountService.register(client), response);
    }

    @PostMapping("admins")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto registerAdmin(
            @RequestBody @Valid AdminDto admin,
            BindingResult result,
            HttpServletResponse response) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        admin.setLogin(admin.getLogin().toLowerCase());

        return setCookieAndReturn(accountService.register(admin), response);
    }


    @PutMapping("admins")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto editAdmin(
            @CookieValue("JAVASESSIONID") String session,
            @RequestBody @Valid AdminEditDto admin,
            BindingResult result) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        return accountService.edit(session, admin);
    }

    @PutMapping("clients")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto editClient(
            @CookieValue("JAVASESSIONID") String session,
            @RequestBody @Valid ClientEditDto client,
            BindingResult result) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        return accountService.edit(session, client);
    }

    @GetMapping("clients")
    @ResponseStatus(HttpStatus.OK)
    public List<AccountDto> getClients(
            @CookieValue("JAVASESSIONID") String session) throws Exception {

        return accountService.getAll(session);
    }

    @GetMapping("accounts")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto getAccount(
            @CookieValue("JAVASESSIONID") String session) throws Exception {

        return accountService.get(session);
    }

    @PostMapping("sessions")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto login(
            @RequestBody @Valid LoginDto account,
            BindingResult result,
            HttpServletResponse response) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        account.setLogin(account.getLogin().toLowerCase());
        String session = accountService.login(account.getLogin(), account.getPassword());
        response.addCookie(new Cookie("JAVASESSIONID", session));

        return accountService.get(session);
    }

    @DeleteMapping("sessions")
    @ResponseStatus(HttpStatus.OK)
    public String logout(
            @CookieValue(value = "JAVASESSIONID", required = false) String session) {

        accountService.logout(session);
        return "{}";
    }

    private AccountDto setCookieAndReturn(Account account, HttpServletResponse response) throws ServiceException {
        response.addCookie(
                new Cookie(
                        "JAVASESSIONID",
                        accountService.login(account.getLogin(), account.getPassword())
                )
        );

        return new AccountDto(account);
    }


}
