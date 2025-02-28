package com.future.orderservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "account-service")
public interface AccountServiceClient {

    @GetMapping("/is-authorized/{userId}")
    ResponseEntity<Map<String, Boolean>> isUserAuthorized(@PathVariable Long userId);
}
