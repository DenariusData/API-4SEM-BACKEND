package data.denarius.radarius.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-test")
public class TestController {
    @GetMapping
    public String test() {
        return "API is working!";
    }

    @GetMapping("/secured")
    public String securedTest() {
        return "Secured API is working!";
    }
}
