package com.example.mongo.dao;

import com.example.mongo.model.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductDaoImpl extends AbstractDAO<Product>{

    public List<Product> findAllProducts(){
        return findAll("product",Product.class);
    }
}
