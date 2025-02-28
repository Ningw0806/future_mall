package com.future.itemservice.controller;

import com.future.futurecommon.payload.ProductStockModRequest;
import com.future.futurecommon.payload.ProductStockModResponse;
import com.future.futurecommon.payload.ProductStockRequest;
import com.future.futurecommon.payload.ProductStockResponse;
import com.future.itemservice.model.Product;
import com.future.itemservice.payload.ProductDTO;
import com.future.itemservice.payload.ProductResponse;
import com.future.itemservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/item-service")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        return new ResponseEntity<>("Hello item service", HttpStatus.OK);
    }

    @PostMapping("/admin/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody Product product) {
        ProductDTO productDTO = productService.addProduct(product);
        return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts() {
        ProductResponse productResponse = productService.getAllProducts();
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductByKeyword(@PathVariable String keyword) {
        ProductResponse productResponse = productService.getProductByKeyword(keyword);
        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }

    @PutMapping("admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody Product product, @PathVariable Long productId) {
        ProductDTO updatedProductDTO = productService.updateProduct(product,productId);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }

    @DeleteMapping("admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct( @PathVariable Long productId) {
        ProductDTO deletedProductDTO = productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProductDTO, HttpStatus.OK);
    }
    //check if a product has enough stock
    @GetMapping("/{productId}/stock")
    public boolean isStockAvailable(@PathVariable Long productId, @RequestParam int quantity) {
        return productService.checkStockAvailability(productId, quantity);
    }
    //check each product in an order to see if this item has enough stock
    @PostMapping("/public/products/check-stock")
    public ResponseEntity<ProductStockResponse> checkStock(@RequestBody ProductStockRequest productRequest) {
        return new ResponseEntity<>( productService.checkStockForManyProducts(productRequest), HttpStatus.OK);
    }

    @PostMapping("/public/products/stock-mod")
    public ResponseEntity<ProductStockModResponse> processProductStock(@RequestBody ProductStockModRequest productStockModRequest) {
        return new ResponseEntity<>(productService.processProducts(productStockModRequest), HttpStatus.OK);
    }
}
