package com.example.orderservice.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OrderCreatedEventPayload {
    private String orderNo;
    private String productName;
    private Integer quantity;
    private String status;
}
