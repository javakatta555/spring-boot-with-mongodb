package com.example.mongo.controller;

import com.example.mongo.config.MongoConfig;
import com.example.mongo.model.Product;
import com.example.mongo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MongoConfig mongoConfig;

    @GetMapping("/healthCheck")
    public String checkHealth(){
        return "I am healthy !";
    }

    @GetMapping("/product")
    public List<Product> getAllProducts(){
        return mongoConfig.getMongoTemplate("Product").findAll(Product.class);
    }

    @PostMapping("/product")
    public Product saveProduct(@RequestBody Product product){
        productRepository.save(product);
        return product;
    }

    @GetMapping("/product/{productName}")
    public Product finaByProductName(@PathVariable String productName){
        return productRepository.findByProductName(productName);
    }

    @GetMapping("/products")
    public Page<Product> getRecords(@RequestParam(required = false,defaultValue = "0") int pageNumber,@RequestParam int pageSize ){
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        return productRepository.findAll(pageable);
    }

}
