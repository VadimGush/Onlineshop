package net.thumbtack.onlineshop;

import net.thumbtack.onlineshop.response.ServerConfigurationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class RequestController {

    private AppConfig config;

    @Autowired
    public RequestController(AppConfig config) {
        this.config = config;
    }

    @RequestMapping(method=GET, name="/api/settings")
    public ServerConfigurationResponse serverConfigurationResponse(@CookieValue("JAVASESSIONID") String sessionId) {
        return new ServerConfigurationResponse(config);
    }

}
