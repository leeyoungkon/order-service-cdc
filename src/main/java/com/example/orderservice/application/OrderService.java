package com.example.orderservice.application;

import com.example.orderservice.api.CreateOrderRequest;
import com.example.orderservice.api.OrderResponse;
import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderRepository;
import com.example.orderservice.outbox.OutboxEvent;
import com.example.orderservice.outbox.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        String orderNo = UUID.randomUUID().toString();
        String normalizedProductName = normalizeProductName(request.getProductName());
        Integer quantity = request.getQuantity();

        Order order = Order.builder()
                .orderNo(orderNo)
                .productName(normalizedProductName)
                .quantity(quantity)
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .build();

        orderRepository.save(order);

        try {
            ObjectNode payloadNode = objectMapper.createObjectNode()
                .put("orderNo", orderNo)
                .put("productName", normalizedProductName)
                .put("quantity", quantity)
                .put("status", "CREATED");

            outboxEventRepository.save(
                    OutboxEvent.of(
                            "Order",
                            orderNo,
                            "OrderCreated",
                        objectMapper.writeValueAsString(payloadNode)
                    )
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize outbox payload", e);
        }

        return new OrderResponse(orderNo, "CREATED");
    }

    private String normalizeProductName(String productName) {
        if (productName == null) {
            return null;
        }
        return productName
                .replace("\r", " ")
                .replace("\n", " ")
                .trim();
    }
}
