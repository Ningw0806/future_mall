package com.future.itemservice.service;

import com.future.itemservice.model.Product;
import com.future.itemservice.payload.ProductDTO;
import com.future.itemservice.payload.ProductResponse;
import com.future.itemservice.payload.ProductStockRequest;
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

     Map<Long, Boolean> checkStockForManyProducts(List<ProductStockRequest> productRequests);

     ProductDTO deleteProduct(Long productId);
}
