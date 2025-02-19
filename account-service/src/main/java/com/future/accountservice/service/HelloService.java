package com.future.accountservice.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    private final OpenFeignClientExample openFeignClientExample;

    public HelloService(OpenFeignClientExample openFeignClientExample) {
        this.openFeignClientExample = openFeignClientExample;
    }

    public String getHelloFromItemService() {
        return openFeignClientExample.getHelloMessage();
    }
}
