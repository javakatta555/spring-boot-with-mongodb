package com.example.mongo.controller;

import com.example.mongo.config.MongoConfig;
import com.example.mongo.model.Product;
import com.example.mongo.repository.ProductRepository;
import com.example.mongo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @GetMapping("/healthCheck")
    public String checkHealth(){
        return "I am healthy !";
    }

    @GetMapping("/product")
    public List<Product> getAllProducts(){
        return productService.findAllProducts();
    }

    @PostMapping("/product")
    public Product saveProduct(@RequestBody Product product){
        productRepository.save(product);
        return product;
    }

    @GetMapping("/product/{productName}")
    public Product finaByProductName(@PathVariable String productName){
        Pageable pageable = PageRequest.of(1,1,Sort.by("name").ascending());
        productRepository.findAll(pageable);
        return productRepository.findByProductName(productName);
    }

    @GetMapping("/products")
    public Page<Product> getRecords(@RequestParam(required = false,defaultValue = "0") int pageNumber,@RequestParam int pageSize ){
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        return productRepository.findAll(pageable);
    }

}
