package net.thumbtack.onlineshop;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config.properties")
public class AppConfig {

    @Value("${rest_http_port}")
    private int port;

    @Value("${max_name_length}")
    private int maxNameLength;

    @Value("${min_password_length}")
    private int minPasswordLength;

    public int getPort() {
        return port;
    }

    public int getMaxNameLength() {
        return maxNameLength;
    }

    public int getMinPasswordLength() {
        return minPasswordLength;
    }
}
