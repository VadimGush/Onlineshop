package net.thumbtack.onlineshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("api")
public class CategoriesController {


    @RequestMapping(method=POST, path="categories")
    @ResponseStatus(HttpStatus.OK)
    public String addCategories() {
        return "{}";
    }

    @RequestMapping(method=GET, value="/categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String getCategoriesById(@PathVariable int id) {
        return String.valueOf(id);
    }

    @RequestMapping(method=PUT, value="/categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String editCategories(@PathVariable int id) {
        return "{}";
    }

    @RequestMapping(method=DELETE, value="/categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteCategories(@PathVariable int id) {
        return "{}";
    }

    @RequestMapping(method=GET, path="categories")
    @ResponseStatus(HttpStatus.OK)
    public String getCategories() {
        return "{}";
    }
}
