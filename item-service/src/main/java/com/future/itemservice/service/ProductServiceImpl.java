package com.future.itemservice.service;

import com.future.futurecommon.constant.ProductStockEventResponseType;
import com.future.futurecommon.constant.ProductStockEventType;
import com.future.futurecommon.payload.*;
import com.future.itemservice.config.ModelMapperConfig;
import com.future.itemservice.model.Product;
import com.future.itemservice.payload.ProductDTO;
import com.future.itemservice.payload.ProductResponse;
import com.future.itemservice.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    ProductRepository productRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public ProductDTO addProduct(Product product) {
        //product.setImage("default.img");
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);

    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> productList = productRepository.findAll();
        List<ProductDTO> productDTOList = productList.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOList);
        return productResponse;
    }

    @Override
    public ProductResponse getProductByKeyword(String keyword) {
        List<Product> products = productRepository.findByNameLikeIgnoreCase("%" + keyword + "%");
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Product product, Long productId) {
        //get existing product
        Product productFromDb = productRepository.findById(productId).orElseThrow(()-> new NoSuchElementException("product not found with id: " + productId));
        //update
        productFromDb.setName(product.getName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setStockQuantity(product.getStockQuantity());
        //productFromDb.setImage(product.getImage());
        //productFromDb.setDiscount(product.getDiscount());
        //save updated product to db
        Product savedProduct = productRepository.save(productFromDb);
        System.out.println("saved product: " + savedProduct);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }


    //check quantity
    public boolean checkStockAvailability(Long productId, int quantity) {
        return productRepository.findById(productId)
                .map(product -> product.getStockQuantity() >= quantity)
                .orElse(false); // If product doesn't exist, return false
    }

    @Override
    public ProductStockResponse checkStockForManyProducts(ProductStockRequest productRequests) {
        List<ProductStockDTO> productStockDTOList = productRequests.getProductStockDTOList();

        boolean result = productStockDTOList.stream()
                .peek(productStockDTO -> productStockDTO.setAvailable(
                        productRepository.findById(productStockDTO.getProductId())
                                .map(product -> product.getStockQuantity() >= productStockDTO.getQuantity())
                                .orElse(false)
                ))
                .allMatch(ProductStockDTO::isAvailable);

        return new ProductStockResponse(productStockDTOList, result);
    }

    @Override
    public ProductStockModResponse processProducts(ProductStockModRequest productStockModRequest) {

        List<Long> productIds = productStockModRequest.getProductStockDTOList()
                .stream()
                .map(ProductStockDTO::getProductId)
                .toList();
        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        if (productMap.size() != productIds.size()) {
            return new ProductStockModResponse(ProductStockEventResponseType.FAIL);
        }

        ProductStockEventType event = productStockModRequest.getProductStockEventType();
        int multiplier = event.equals(ProductStockEventType.ADD_PRODUCT_STOCK_EVENT) ? 1 : -1;

        // future need to handle stock quantity < 0
        productStockModRequest.getProductStockDTOList().forEach(productStockDTO -> {
            Product product = productMap.get(productStockDTO.getProductId());
            product.setStockQuantity(product.getStockQuantity() + multiplier * productStockDTO.getQuantity());
        });

        // Batch save all updated products
        productRepository.saveAll(productMap.values());

        return new ProductStockModResponse(ProductStockEventResponseType.SUCCESS);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(()->new NoSuchElementException("product not found with id: " + productId));
        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }
}
