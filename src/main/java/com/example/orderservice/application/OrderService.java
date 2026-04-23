package com.example.orderservice.application;

import com.example.orderservice.api.CreateOrderRequest;
import com.example.orderservice.api.OrderResponse;
import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderRepository;
import com.example.orderservice.outbox.OutboxEvent;
import com.example.orderservice.outbox.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        Order order = Order.builder()
                .orderNo(orderNo)
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .status("CREATED")
                .createdAt(LocalDateTime.now())
                .build();

        orderRepository.save(order);

        OrderCreatedEventPayload payload = OrderCreatedEventPayload.builder()
                .orderNo(orderNo)
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .status("CREATED")
                .build();

        try {
            outboxEventRepository.save(
                    OutboxEvent.of(
                            "Order",
                            orderNo,
                            "OrderCreated",
                            objectMapper.writeValueAsString(payload)
                    )
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize outbox payload", e);
        }

        return new OrderResponse(orderNo, "CREATED");
    }
}
