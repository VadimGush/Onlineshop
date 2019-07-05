package net.thumbtack.onlineshop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * RequestController - отвечает за запросы связанные с управлением сервером и
 * сессиями пользователей, а так же обработкой ошибок.
 */
@RestController
@RequestMapping("api")
public class RequestController {

	@Value("${max_name_length}")
	private int maxNameLength;

	@Value("${min_password_length}")
	private int minPasswordLength;

    @GetMapping("settings")
    @ResponseStatus(HttpStatus.OK)
    public String serverConfigurationResponse() {
        return "{}";
    }

    @PostMapping("debug/clear")
    @ResponseStatus(HttpStatus.OK)
    public String clearDatabase() {
        return "{}";
    }

    @PostMapping("sessions")
    @ResponseStatus(HttpStatus.OK)
    public String login() {
        return "{}";
    }

    @DeleteMapping("sessions")
    @ResponseStatus(HttpStatus.OK)
    public String logout() {
        return "{}";
    }


}
