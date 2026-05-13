package com.fashora.dto;

import com.fashora.entity.Order;
import com.fashora.entity.OrderItem;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderDto {

    private Long id;
    private String orderNumber;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal deliveryCharge;
    private String couponCode;
    private String paymentMethod;
    private String shippingAddress;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;

    @Data
    public static class OrderItemDto {
        private Long productId;
        private String productName;
        private String brand;
        private String imageEmoji;
        private Integer quantity;
        private String size;
        private BigDecimal price;
    }

    @Data
    public static class PlaceOrderRequest {
        private String shippingAddress;
        private String paymentMethod;
        private String couponCode;
    }

    public static OrderDto from(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setDeliveryCharge(order.getDeliveryCharge());
        dto.setCouponCode(order.getCouponCode());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCreatedAt(order.getCreatedAt());
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream().map(OrderDto::fromItem).collect(Collectors.toList()));
        }
        return dto;
    }

    private static OrderItemDto fromItem(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setBrand(item.getProduct().getBrand());
        dto.setImageEmoji(item.getProduct().getImageEmoji());
        dto.setQuantity(item.getQuantity());
        dto.setSize(item.getSize());
        dto.setPrice(item.getPrice());
        return dto;
    }
}
