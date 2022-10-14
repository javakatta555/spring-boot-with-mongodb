package com.example.mongo.repository;

import com.example.mongo.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface ProductRepository extends MongoRepository<Product,String> {

    @Query("{'productName':?0}")
   Product findByProductName(String productName);
}
