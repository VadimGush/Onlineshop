package net.thumbtack.onlineshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("api")
public class ClientController {

    @RequestMapping(method=POST, path="clients")
    @ResponseStatus(HttpStatus.OK)
    public String registerClient() {
        return "{}";
    }

    @RequestMapping(method=GET, path="accounts")
    @ResponseStatus(HttpStatus.OK)
    public String getAccount() {
        return "{}";
    }

    @RequestMapping(method=PUT, path="clients")
    @ResponseStatus(HttpStatus.OK)
    public String editClient() {
        return "{}";
    }

    @RequestMapping(method=PUT, path="deposits")
    @ResponseStatus(HttpStatus.OK)
    public String putDeposit() {
        return "{}";
    }

    @RequestMapping(method=GET, path="deposits")
    @ResponseStatus(HttpStatus.OK)
    public String getDeposit() {
        return "{}";
    }

    @RequestMapping(method=POST, path="purchases")
    @ResponseStatus(HttpStatus.OK)
    public String buyProduct() {
        return "{}";
    }

    @RequestMapping(method=POST, path="baskets")
    @ResponseStatus(HttpStatus.OK)
    public String addToBasket() {
        return "{}";
    }

    @RequestMapping(method=DELETE, value="/baskets/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteFromBasket(@PathVariable int id) {
        return "{}";
    }

    @RequestMapping(method=PUT, path="baskets")
    @ResponseStatus(HttpStatus.OK)
    public String editProductCount() {
        return "{}";
    }

    @RequestMapping(method=GET, path="baskets")
    @ResponseStatus(HttpStatus.OK)
    public String getBasket() {
        return "{}";
    }

    @RequestMapping(method=POST, path="purchases/baskets")
    @ResponseStatus(HttpStatus.OK)
    public String buyBasket() {
        return "{}";
    }
}
