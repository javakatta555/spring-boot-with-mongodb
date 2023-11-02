package com.example.mongo.service;

import com.example.mongo.dao.ProductDaoImpl;
import com.example.mongo.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductDaoImpl productDao;

    public List<Product> findAllProducts(){
        return productDao.findAllProducts();
    }
}
