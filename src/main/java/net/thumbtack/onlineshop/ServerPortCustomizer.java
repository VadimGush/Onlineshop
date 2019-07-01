package net.thumbtack.onlineshop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class ServerPortCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    private AppConfig config;

    @Autowired
    public ServerPortCustomizer(AppConfig config) {
        this.config = config;
    }

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        factory.setPort(config.getPort());
    }

}
