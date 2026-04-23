package com.example.orderservice.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {
    private String productName;
    private Integer quantity;
}
