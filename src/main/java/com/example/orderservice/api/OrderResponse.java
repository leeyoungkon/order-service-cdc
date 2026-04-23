package com.example.orderservice.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderResponse {
    private String orderNo;
    private String status;
}
