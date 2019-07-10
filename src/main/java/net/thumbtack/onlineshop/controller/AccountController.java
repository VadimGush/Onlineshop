package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.controller.validation.ValidationException;
import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.dto.*;
import net.thumbtack.onlineshop.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
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
    public ClientDto registerClient(
            @RequestBody @Valid ClientDto client,
            BindingResult result,
            HttpServletResponse response) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        client.setLogin(client.getLogin().toLowerCase());
        Account account = accountService.register(client);

        response.addCookie(new Cookie("JAVASESSIONID",
                accountService.login(account.getLogin(), account.getPassword())));

        return new ClientDto(account);
    }

    @PostMapping("admins")
    @ResponseStatus(HttpStatus.OK)
    public AdminDto registerAdmin(
            @RequestBody @Valid AdminDto admin,
            BindingResult result,
            HttpServletResponse response) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        admin.setLogin(admin.getLogin().toLowerCase());
        Account account = accountService.register(admin);

        response.addCookie(new Cookie("JAVASESSIONID",
                accountService.login(account.getLogin(), account.getPassword())));

        return new AdminDto(account);
    }

    @PutMapping("admins")
    @ResponseStatus(HttpStatus.OK)
    public AdminDto editAdmin(
            @CookieValue("JAVASESSIONID") String session,
            @RequestBody @Valid AdminEditDto admin,
            BindingResult result) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        return new AdminDto(accountService.edit(session, admin));
    }

    @PutMapping("clients")
    @ResponseStatus(HttpStatus.OK)
    public ClientDto editClient(
            @CookieValue("JAVASESSIONID") String session,
            @RequestBody @Valid ClientEditDto client,
            BindingResult result) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        return new ClientDto(accountService.edit(session, client));
    }

    @GetMapping("clients")
    @ResponseStatus(HttpStatus.OK)
    public List<AccountDto> getClients(
            @CookieValue("JAVASESSIONID") String session) throws Exception {

        List<Account> accounts = accountService.getAll(session);
        List<AccountDto> result = new ArrayList<>();

        accounts.forEach((account) -> result.add(new AccountDto(account)));

        return result;
    }

    @GetMapping("accounts")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto getAccount(
            @CookieValue("JAVASESSIONID") String session) throws Exception {

        return new AccountDto(accountService.getAccount(session));
    }

    @PostMapping("sessions")
    @ResponseStatus(HttpStatus.OK)
    public String login(
            @RequestBody @Valid LoginDto account,
            BindingResult result,
            HttpServletResponse response) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        account.setLogin(account.getLogin().toLowerCase());
        String session = accountService.login(account.getLogin(), account.getPassword());
        response.addCookie(new Cookie("JAVASESSIONID", session));

        return "{}";
    }

    @DeleteMapping("sessions")
    @ResponseStatus(HttpStatus.OK)
    public String logout(
            @CookieValue("JAVASESSIONID") String session) {

        accountService.logout(session);
        return "{}";
    }




}