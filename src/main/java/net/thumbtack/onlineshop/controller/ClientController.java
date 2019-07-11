package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.controller.validation.ValidationException;
import net.thumbtack.onlineshop.database.models.Basket;
import net.thumbtack.onlineshop.dto.*;
import net.thumbtack.onlineshop.service.AccountService;
import net.thumbtack.onlineshop.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
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

        if (result.hasErrors())
            throw new ValidationException(result);

        clientService.putDeposit(session, deposit.getDeposit());

        return new AccountDto(
                accountService.getAccount(session)
        );
    }

    @GetMapping("deposits")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto getDeposit(
            @CookieValue("JAVASESSIONID") String session) throws Exception {

        // TODO: Удалить из AccountService метод getDeposit
        return new AccountDto(
                accountService.getAccount(session)
        );
    }

    @PostMapping("purchases")
    @ResponseStatus(HttpStatus.OK)
    public BuyProductDto buyProduct(
            @CookieValue("JAVASESSIONID") String session,
            @RequestBody @Valid BuyProductDto product,
            BindingResult result) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        return clientService.buyProduct(session, product);
    }

    @PostMapping("baskets")
    @ResponseStatus(HttpStatus.OK)
    public List<BuyProductDto> addToBasket(
            @CookieValue("JAVASESSIONID") String session,
            @RequestBody @Valid BuyProductDto product,
            BindingResult result) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        List<Basket> basket = clientService.addToBasket(session, product);

        return getBasket(basket);
    }


    @DeleteMapping("baskets/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteFromBasket(
            @CookieValue("JAVASESSIONID") String session,
            @PathVariable int id) throws Exception {

        clientService.deleteFromBasket(session, id);

        return "{}";
    }

    @PutMapping("baskets")
    @ResponseStatus(HttpStatus.OK)
    public List<BuyProductDto> editProductCount(
            @CookieValue("JAVASESSIONID") String session,
            @RequestBody @Valid BuyProductDto product,
            BindingResult result) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        List<Basket> basket =  clientService.editProductCount(session, product);

        return getBasket(basket);
    }

    @GetMapping("baskets")
    @ResponseStatus(HttpStatus.OK)
    public List<BuyProductDto> getBasket(
            @CookieValue("JAVASESSIONID") String session) throws Exception {

        return getBasket(clientService.getBasket(session));

    }

    @PostMapping("purchases/baskets")
    @ResponseStatus(HttpStatus.OK)
    public ResultBasketDto buyBasket(
            @CookieValue("JAVASESSIONID") String session,
            @RequestBody @Valid List<BuyProductDto> toBuy,
            BindingResult result) throws Exception {

        if (result.hasErrors())
            throw new ValidationException(result);

        Pair<List<BuyProductDto>, List<Basket>> basket = clientService.buyBasket(session, toBuy);

        return new ResultBasketDto(basket.getFirst(), basket.getSecond());
    }

    private List<BuyProductDto> getBasket(List<Basket> basket) {
        List<BuyProductDto> result = new ArrayList<>();

        for (Basket entity : basket) {
            result.add(
                    new BuyProductDto(
                            entity.getProduct().getId(),
                            entity.getProduct().getName(),
                            entity.getProduct().getPrice(),
                            entity.getCount()
                    )
            );
        }
        return result;
    }

}
