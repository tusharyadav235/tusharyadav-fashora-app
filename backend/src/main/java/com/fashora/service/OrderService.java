package com.fashora.service;

import com.fashora.dto.OrderDto;
import com.fashora.entity.*;
import com.fashora.exception.BadRequestException;
import com.fashora.exception.ResourceNotFoundException;
import com.fashora.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepo;
    private final CartItemRepository cartRepo;
    private final UserRepository userRepo;
    private final CartService cartService;

    @Transactional
    public OrderDto placeOrder(String email, OrderDto.PlaceOrderRequest req) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<CartItem> cartItems = cartRepo.findByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        BigDecimal subtotal = cartItems.stream()
                .map(i -> i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = subtotal.compareTo(BigDecimal.valueOf(499)) > 0
                ? subtotal.multiply(BigDecimal.valueOf(0.20)).setScale(2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal delivery = subtotal.compareTo(BigDecimal.valueOf(499)) > 0
                ? BigDecimal.ZERO : BigDecimal.valueOf(49);

        BigDecimal total = subtotal.subtract(discount).add(delivery);

        Order order = Order.builder()
                .user(user)
                .orderNumber("FSH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .status(Order.OrderStatus.CONFIRMED)
                .totalAmount(total)
                .discountAmount(discount)
                .deliveryCharge(delivery)
                .couponCode(req.getCouponCode())
                .paymentMethod(req.getPaymentMethod())
                .shippingAddress(req.getShippingAddress())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepo.save(order);

        List<OrderItem> orderItems = cartItems.stream().map(ci -> OrderItem.builder()
                .order(savedOrder)
                .product(ci.getProduct())
                .quantity(ci.getQuantity())
                .size(ci.getSize())
                .color(ci.getColor())
                .price(ci.getProduct().getPrice())
                .build()).collect(Collectors.toList());

        savedOrder.setItems(orderItems);
        orderRepo.save(savedOrder);

        cartService.clearCart(email);

        return OrderDto.from(savedOrder);
    }

    public List<OrderDto> getUserOrders(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepo.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(OrderDto::from).collect(Collectors.toList());
    }

    public OrderDto getOrderById(Long id, String email) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUser().getEmail().equals(email)) {
            throw new BadRequestException("Unauthorized");
        }
        return OrderDto.from(order);
    }
}
