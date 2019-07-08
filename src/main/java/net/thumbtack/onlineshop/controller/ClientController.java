package net.thumbtack.onlineshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class ClientController {

    @PostMapping("clients")
    @ResponseStatus(HttpStatus.OK)
    public String registerClient() {
        return "{}";
    }

    @GetMapping("accounts")
    @ResponseStatus(HttpStatus.OK)
    public String getAccount() {
        return "{}";
    }

    @PutMapping("clients")
    @ResponseStatus(HttpStatus.OK)
    public String editClient() {
        return "{}";
    }

    @PutMapping("deposits")
    @ResponseStatus(HttpStatus.OK)
    public String putDeposit() {
        return "{}";
    }

    @GetMapping("deposits")
    @ResponseStatus(HttpStatus.OK)
    public String getDeposit() {
        return "{}";
    }

    @PostMapping("purchases")
    @ResponseStatus(HttpStatus.OK)
    public String buyProduct() {
        return "{}";
    }

    @PostMapping("baskets")
    @ResponseStatus(HttpStatus.OK)
    public String addToBasket() {
        return "{}";
    }

    @DeleteMapping("baskets/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteFromBasket(@PathVariable int id) {
        return "{}";
    }

    @PutMapping("baskets")
    @ResponseStatus(HttpStatus.OK)
    public String editProductCount() {
        return "{}";
    }

    @GetMapping("baskets")
    @ResponseStatus(HttpStatus.OK)
    public String getBasket() {
        return "{}";
    }

    @PostMapping("purchases/baskets")
    @ResponseStatus(HttpStatus.OK)
    public String buyBasket() {
        return "{}";
    }
}
