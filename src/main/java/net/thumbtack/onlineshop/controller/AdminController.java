package net.thumbtack.onlineshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("api")
public class AdminController {

    @RequestMapping(method=POST, path="admins")
    @ResponseStatus(HttpStatus.OK)
    public String registerAdmin() {
        return "{}";
    }

    @RequestMapping(method=PUT, path="admins")
    @ResponseStatus(HttpStatus.OK)
    public String editAdmin() {
        return "{}";
    }

    @RequestMapping(method=GET, path="clients")
    @ResponseStatus(HttpStatus.OK)
    public String getClients() {
        return "{}";
    }

}
