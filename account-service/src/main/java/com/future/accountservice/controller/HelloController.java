package com.future.accountservice.controller;

import com.future.accountservice.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account-service")
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        String helloMessageFromItemService = helloService.getHelloFromItemService();
        logger.info(helloMessageFromItemService);

        return new ResponseEntity<>("Hello account service " + helloMessageFromItemService, HttpStatus.OK);
    }
}
