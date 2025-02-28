package com.future.itemservice.service;

import com.future.futurecommon.payload.ProductStockModRequest;
import com.future.futurecommon.payload.ProductStockModResponse;
import com.future.futurecommon.payload.ProductStockRequest;
import com.future.futurecommon.payload.ProductStockResponse;
import com.future.itemservice.model.Product;
import com.future.itemservice.payload.ProductDTO;
import com.future.itemservice.payload.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ProductService {
     ProductDTO addProduct(Product product);

     ProductResponse getAllProducts();

     ProductResponse getProductByKeyword(String keyword);

     ProductDTO updateProduct(Product product, Long productId);

     boolean checkStockAvailability(Long productId, int quantity);

     ProductStockResponse checkStockForManyProducts(ProductStockRequest productRequests);

     ProductStockModResponse processProducts(ProductStockModRequest productStockModRequest);

     ProductDTO deleteProduct(Long productId);
}
