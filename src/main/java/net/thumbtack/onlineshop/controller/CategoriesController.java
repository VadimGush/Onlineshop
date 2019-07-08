package net.thumbtack.onlineshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class CategoriesController {


    @PostMapping("categories")
    @ResponseStatus(HttpStatus.OK)
    public String addCategories() {
        return "{}";
    }

    @GetMapping("categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String getCategoriesById(@PathVariable int id) {
        return String.valueOf(id);
    }

    @PutMapping("categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String editCategories(@PathVariable int id) {
        return "{}";
    }

    @DeleteMapping("categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteCategories(@PathVariable int id) {
        return "{}";
    }

    @GetMapping("categories")
    @ResponseStatus(HttpStatus.OK)
    public String getCategories() {
        return "{}";
    }
}
