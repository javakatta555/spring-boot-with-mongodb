package com.example.mongo.service;

import com.example.mongo.dao.OrderDaoImpl;
import com.example.mongo.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDaoImpl orderDao;

    public List<Order> findAllOrder(){
        return orderDao.findAll("order",Order.class);
    }

    public Order saveOrder(Order order){
        orderDao.save("order",order);
        return order;
    }
}
