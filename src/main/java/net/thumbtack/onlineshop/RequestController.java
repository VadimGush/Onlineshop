package net.thumbtack.onlineshop;

import net.thumbtack.onlineshop.response.Greeting;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class RequestController {

    private static final String template = "Hello, %s";
    private final AtomicInteger counter = new AtomicInteger();

    @RequestMapping(method=GET, name="/greetings")
    public Greeting greeting(@RequestParam(value="name", defaultValue="world") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

}
