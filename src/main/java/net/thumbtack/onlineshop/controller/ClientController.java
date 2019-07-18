package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.dto.AccountDto;
import net.thumbtack.onlineshop.dto.DepositDto;
import net.thumbtack.onlineshop.dto.ProductDto;
import net.thumbtack.onlineshop.dto.ResultBasketDto;
import net.thumbtack.onlineshop.dto.actions.Register;
import net.thumbtack.onlineshop.dto.validation.ValidationException;
import net.thumbtack.onlineshop.service.AccountService;
import net.thumbtack.onlineshop.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api")
public class ClientController {

    private ClientService clientService;
    private AccountService accountService;

    @Autowired
    public ClientController(ClientService clientService, AccountService accountService) {
        this.clientService = clientService;
        this.accountService = accountService;
    }

    @PutMapping("deposits")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto putDeposit(
            @CookieValue("JAVASESSIONID") String session,
            @RequestBody @Valid DepositDto deposit,
            BindingResult result) throws Exception {

        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        return clientService.putDeposit(session, deposit.getDeposit());
    }

    @GetMapping("deposits")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto getDeposit(
            @CookieValue("JAVASESSIONID") String session) throws Exception {

        return new AccountDto(accountService.getAccount(session));
    }

    @PostMapping("purchases")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto buyProduct(
            @CookieValue("JAVASESSIONID") String session,
            @RequestBody @Validated(Register.class) ProductDto product,
            BindingResult result) throws Exception {

        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        if (product.getCount() == null) {
            product.setCount(1);
        }

        return clientService.buyProduct(session, product);
    }

    @PostMapping("baskets")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDto> addToBasket(
            @CookieValue("JAVASESSIONID") String session,
            @RequestBody @Validated(Register.class) ProductDto product,
            BindingResult result) throws Exception {

        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        if (product.getCount() == null) {
            product.setCount(1);
        }

        return clientService.addToBasket(session, product);
    }


    @DeleteMapping("baskets/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteFromBasket(
            @CookieValue("JAVASESSIONID") String session,
            @PathVariable int id) throws Exception {

        clientService.deleteFromBasket(session, id);

        return "{}";
    }

    // TODO: Здесь поле количества должно быть обязательным

    @PutMapping("baskets")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDto> editProductCount(
            @CookieValue("JAVASESSIONID") String session,
            @RequestBody @Valid ProductDto product,
            BindingResult result) throws Exception {

        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        return clientService.editProductCount(session, product);
    }

    @GetMapping("baskets")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDto> getBasket(
            @CookieValue("JAVASESSIONID") String session) throws Exception {

        return clientService.getBasket(session);
    }

    @PostMapping("purchases/baskets")
    @ResponseStatus(HttpStatus.OK)
    public ResultBasketDto buyBasket(
            @CookieValue("JAVASESSIONID") String session,
            @RequestBody @Validated(Register.class) List<ProductDto> toBuy,
            BindingResult result) throws Exception {

        if (result.hasErrors()) {
            throw new ValidationException(result);
        }

        return clientService.buyBasket(session, toBuy);
    }

}
