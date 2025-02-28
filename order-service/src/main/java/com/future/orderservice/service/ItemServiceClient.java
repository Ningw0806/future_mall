package com.future.orderservice.service;

import com.future.futurecommon.payload.ProductStockModRequest;
import com.future.futurecommon.payload.ProductStockModResponse;
import com.future.futurecommon.payload.ProductStockRequest;
import com.future.futurecommon.payload.ProductStockResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "item-service")
public interface ItemServiceClient {
    @PostMapping("/api/v1/item-service/public/products/stock-mod")
    ResponseEntity<ProductStockModResponse> processProductStock(@RequestBody ProductStockModRequest productStockModRequest);

    @PostMapping("/api/v1/item-service/public/products/check-stock")
    ResponseEntity<ProductStockResponse> checkStock(@RequestBody ProductStockRequest productRequest);


}
