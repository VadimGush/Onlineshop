package net.thumbtack.onlineshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("api")
public class ProductController {

    @RequestMapping(method=POST, path="products")
    @ResponseStatus(HttpStatus.OK)
    public String addProduct() {
        return "{}";
    }

    @RequestMapping(method=PUT, value="/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String editProduct(@PathVariable int id) {
        return "{}";
    }

    @RequestMapping(method=DELETE, value="/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteProduct(@PathVariable int id) {
        return "{}";
    }

    @RequestMapping(method=GET, value="/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String getProduct(@PathVariable int id) {
        return "{}";
    }

    @RequestMapping(method=GET, path="products")
    @ResponseStatus(HttpStatus.OK)
    public String getProducts() {
        return "{}";
    }

}
