package com.fashora.service;

import com.fashora.dto.CartDto;
import com.fashora.entity.CartItem;
import com.fashora.entity.Product;
import com.fashora.entity.User;
import com.fashora.exception.ResourceNotFoundException;
import com.fashora.repository.CartItemRepository;
import com.fashora.repository.ProductRepository;
import com.fashora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public CartDto.CartResponse getCart(String email) {
        User user = getUser(email);
        List<CartItem> items = cartRepo.findByUserId(user.getId());
        return buildCartResponse(items);
    }

    @Transactional
    public CartDto.CartResponse addItem(String email, CartDto.AddItemRequest req) {
        User user = getUser(email);
        Product product = productRepo.findById(req.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Optional<CartItem> existing = cartRepo.findByUserIdAndProductIdAndSize(
                user.getId(), product.getId(), req.getSize());

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + req.getQuantity());
            cartRepo.save(existing.get());
        } else {
            CartItem item = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(req.getQuantity())
                    .size(req.getSize())
                    .color(req.getColor())
                    .build();
            cartRepo.save(item);
        }
        return getCart(email);
    }

    @Transactional
    public CartDto.CartResponse updateItem(String email, Long cartItemId, CartDto.UpdateItemRequest req) {
        CartItem item = cartRepo.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (req.getQuantity() <= 0) {
            cartRepo.delete(item);
        } else {
            item.setQuantity(req.getQuantity());
            cartRepo.save(item);
        }
        return getCart(email);
    }

    @Transactional
    public CartDto.CartResponse removeItem(String email, Long cartItemId) {
        cartRepo.deleteById(cartItemId);
        return getCart(email);
    }

    @Transactional
    public void clearCart(String email) {
        User user = getUser(email);
        cartRepo.deleteByUserId(user.getId());
    }

    private CartDto.CartResponse buildCartResponse(List<CartItem> items) {
        List<CartDto.CartItemResponse> itemDtos = items.stream().map(item -> {
            CartDto.CartItemResponse r = new CartDto.CartItemResponse();
            r.setCartItemId(item.getId());
            r.setProductId(item.getProduct().getId());
            r.setProductName(item.getProduct().getName());
            r.setBrand(item.getProduct().getBrand());
            r.setImageEmoji(item.getProduct().getImageEmoji());
            r.setPrice(item.getProduct().getPrice());
            r.setOriginalPrice(item.getProduct().getOriginalPrice());
            r.setQuantity(item.getQuantity());
            r.setSize(item.getSize());
            r.setColor(item.getColor());
            r.setSubtotal(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            return r;
        }).collect(Collectors.toList());

        BigDecimal subtotal = itemDtos.stream()
                .map(CartDto.CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = subtotal.compareTo(BigDecimal.valueOf(499)) > 0
                ? subtotal.multiply(BigDecimal.valueOf(0.20)).setScale(2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal delivery = subtotal.compareTo(BigDecimal.valueOf(499)) > 0
                ? BigDecimal.ZERO : BigDecimal.valueOf(49);

        BigDecimal total = subtotal.subtract(discount).add(delivery);

        CartDto.CartResponse response = new CartDto.CartResponse();
        response.setItems(itemDtos);
        response.setTotalItems(items.stream().mapToInt(CartItem::getQuantity).sum());
        response.setSubtotal(subtotal);
        response.setDiscount(discount);
        response.setDeliveryCharge(delivery);
        response.setTotal(total);
        return response;
    }

    private User getUser(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
