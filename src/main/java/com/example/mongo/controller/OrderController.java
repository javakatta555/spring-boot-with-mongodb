package com.example.mongo.controller;

import com.example.mongo.model.Order;
import com.example.mongo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/order")
    public List<Order> findAll(){
        return orderService.findAllOrder();
    }

    @PostMapping("/order")
    public Order saveOrder(@RequestBody Order order){
        return orderService.saveOrder(order);
    }
}
