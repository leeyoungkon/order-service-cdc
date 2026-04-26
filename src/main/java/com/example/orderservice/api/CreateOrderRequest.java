package com.example.orderservice.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {
    @NotBlank
    private String productName;

    @NotNull
    @Min(1)
    private Integer quantity;
}
