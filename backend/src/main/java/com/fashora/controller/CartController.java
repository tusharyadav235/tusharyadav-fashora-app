package com.fashora.controller;

import com.fashora.dto.ApiResponse;
import com.fashora.dto.CartDto;
import com.fashora.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartDto.CartResponse>> getCart(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(cartService.getCart(user.getUsername())));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartDto.CartResponse>> addItem(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody CartDto.AddItemRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Item added", cartService.addItem(user.getUsername(), req)));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<CartDto.CartResponse>> updateItem(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            @RequestBody CartDto.UpdateItemRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Cart updated", cartService.updateItem(user.getUsername(), id, req)));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<CartDto.CartResponse>> removeItem(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Item removed", cartService.removeItem(user.getUsername(), id)));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal UserDetails user) {
        cartService.clearCart(user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared", null));
    }
}
