package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.dto.ServerConfigurationDto;
import net.thumbtack.onlineshop.service.ServerControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * RequestController - отвечает за запросы связанные с управлением сервером и
 * сессиями пользователей, а так же обработкой ошибок.
 */
@RestController
@RequestMapping("api")
public class ServerController {

    @Value("${max_name_length}")
    private int maxNameLength;

    @Value("${min_password_length}")
    private int minPasswordLength;

    @Value("${debug}")
    private boolean debug;

    private ServerControlService serverControl;

    @Autowired
    public ServerController(ServerControlService serverControl) {
        this.serverControl = serverControl;
    }

    @GetMapping("settings")
    @ResponseStatus(HttpStatus.OK)
    public ServerConfigurationDto getServerConfiguration() {
        return new ServerConfigurationDto(maxNameLength, minPasswordLength);
    }

    @PostMapping("debug/clear")
    @ResponseStatus(HttpStatus.OK)
    public String clearDatabase() {
        if (debug) {
            serverControl.clear();
        }
        return "{}";
    }


}
