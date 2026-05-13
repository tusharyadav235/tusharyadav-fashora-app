package com.fashora.controller;

import com.fashora.dto.ApiResponse;
import com.fashora.dto.OrderDto;
import com.fashora.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> placeOrder(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody OrderDto.PlaceOrderRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Order placed!", orderService.placeOrder(user.getUsername(), req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDto>>> getOrders(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getUserOrders(user.getUsername())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrder(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrderById(id, user.getUsername())));
    }
}
