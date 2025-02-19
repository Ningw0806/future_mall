package com.future.accountservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

// Call Microservices directly using its Eureka service name
@FeignClient(name = "item-service")
public interface OpenFeignClientExample {
    @GetMapping("/api/v1/item-service/hello")
    String getHelloMessage();
}
