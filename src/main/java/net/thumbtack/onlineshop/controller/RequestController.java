package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.AppConfig;
import net.thumbtack.onlineshop.response.ServerConfigurationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.*;

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

    @RequestMapping(method=GET, path="settings")
    @ResponseStatus(HttpStatus.OK)
    public ServerConfigurationResponse serverConfigurationResponse() {
        return new ServerConfigurationResponse(maxNameLength, minPasswordLength);
    }

    @RequestMapping(method=POST, path="debug/clear")
    @ResponseStatus(HttpStatus.OK)
    public String clearDatabase() {
        return "{}";
    }

    @RequestMapping(method=POST, path="sessions")
    @ResponseStatus(HttpStatus.OK)
    public String login() {
        return "{}";
    }

    @RequestMapping(method=DELETE, path="sessions")
    @ResponseStatus(HttpStatus.OK)
    public String logout() {
        return "{}";
    }


}
