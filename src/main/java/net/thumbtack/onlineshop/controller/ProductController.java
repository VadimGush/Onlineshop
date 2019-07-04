package net.thumbtack.onlineshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("api")
public class ProductController {

    @PostMapping("products")
    @ResponseStatus(HttpStatus.OK)
    public String addProduct() {
        return "{}";
    }

    @PutMapping("products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String editProduct(@PathVariable int id) {
        return "{}";
    }

    @DeleteMapping("products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteProduct(@PathVariable int id) {
        return "{}";
    }

    @GetMapping("products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String getProduct(@PathVariable int id) {
        return "{}";
    }

    @GetMapping("products")
    @ResponseStatus(HttpStatus.OK)
    public String getProducts() {
        return "{}";
    }

}
