package net.thumbtack.onlineshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api")
public class CloudController {

    /**
     * Уникальный ID сервера для дебага
     */
    private String serverId;

    public CloudController() {
        serverId = UUID.randomUUID().toString();
    }

    /**
     * Yandex Cloud для проверки работоспособности сервера делает GET /api/status
     * каждую секунду.
     *
     * @return идентификатор сервера
     */
    @GetMapping("status")
    @ResponseStatus(HttpStatus.OK)
    public String getStatus() {
        return "Server id: " + serverId;
    }

}
