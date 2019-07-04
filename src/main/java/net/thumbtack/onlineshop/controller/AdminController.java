package net.thumbtack.onlineshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("api")
public class AdminController {

    @PostMapping("admins")
    @ResponseStatus(HttpStatus.OK)
    public String registerAdmin() {
        return "{}";
    }

    @PutMapping("admins")
    @ResponseStatus(HttpStatus.OK)
    public String editAdmin() {
        return "{}";
    }

    @GetMapping("clients")
    @ResponseStatus(HttpStatus.OK)
    public String getClients() {
        return "{}";
    }

}
