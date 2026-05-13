package com.fashora.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

public class CartDto {

    @Data
    public static class AddItemRequest {
        @NotNull
        private Long productId;

        @Min(1)
        private Integer quantity = 1;

        private String size = "M";
        private String color;
    }

    @Data
    public static class UpdateItemRequest {
        @Min(0)
        private Integer quantity;
    }

    @Data
    public static class CartItemResponse {
        private Long cartItemId;
        private Long productId;
        private String productName;
        private String brand;
        private String imageEmoji;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private Integer quantity;
        private String size;
        private String color;
        private BigDecimal subtotal;
    }

    @Data
    public static class CartResponse {
        private List<CartItemResponse> items;
        private int totalItems;
        private BigDecimal subtotal;
        private BigDecimal discount;
        private BigDecimal deliveryCharge;
        private BigDecimal total;
    }
}
