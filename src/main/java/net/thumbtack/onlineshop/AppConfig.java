package net.thumbtack.onlineshop;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

@PropertySource("classpath:config.properties")
@Configuration
@EnableAsync
public class AppConfig {

}
