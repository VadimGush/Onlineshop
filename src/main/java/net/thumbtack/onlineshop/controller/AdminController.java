package net.thumbtack.onlineshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class AdminController {

    @PostMapping("admins")
    @ResponseStatus(HttpStatus.OK)
    public String registerAdmin() {
        // Account current = AdminService.register(...)
        // return SessionService.login(current)
        return "{}";
    }

    @PutMapping("admins")
    @ResponseStatus(HttpStatus.OK)
    public String editAdmin() {
        // return AdminService.edit(session, admin)
        return "{}";
    }

    @GetMapping("clients")
    @ResponseStatus(HttpStatus.OK)
    public String getClients() {
        return "{}";
    }

}
