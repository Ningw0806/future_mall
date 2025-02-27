package com.future.itemservice.service;

import com.future.itemservice.config.ModelMapperConfig;
import com.future.itemservice.model.Product;
import com.future.itemservice.payload.ProductDTO;
import com.future.itemservice.payload.ProductResponse;
import com.future.itemservice.payload.ProductStockRequest;
import com.future.itemservice.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
    public Map<Long, Boolean> checkStockForManyProducts(List<ProductStockRequest> productRequests) {
        return productRequests.stream()
                .collect(Collectors.toMap(
                        ProductStockRequest::getProductId,
                        request -> productRepository.findById(request.getProductId())
                                .map(product -> product.getStockQuantity() >= request.getQuantity())
                                .orElse(false)
                ));
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(()->new NoSuchElementException("product not found with id: " + productId));
        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }
}
