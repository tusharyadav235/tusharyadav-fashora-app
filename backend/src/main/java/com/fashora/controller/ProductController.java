package com.fashora.controller;

import com.fashora.dto.ApiResponse;
import com.fashora.dto.ProductDto;
import com.fashora.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getAll(page, size, sortBy, sortDir)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getById(id)));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getByCategory(category, page, size)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(ApiResponse.ok(productService.search(q, page, size)));
    }

    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<List<ProductDto>>> trending() {
        return ResponseEntity.ok(ApiResponse.ok(productService.getTrending()));
    }

    @GetMapping("/new-arrivals")
    public ResponseEntity<ApiResponse<List<ProductDto>>> newArrivals() {
        return ResponseEntity.ok(ApiResponse.ok(productService.getNewArrivals()));
    }

    @GetMapping("/sale")
    public ResponseEntity<ApiResponse<List<ProductDto>>> sale() {
        return ResponseEntity.ok(ApiResponse.ok(productService.getSaleProducts()));
    }

    @GetMapping("/badge/{badge}")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> byBadge(
            @PathVariable String badge,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getByBadge(badge, page, size)));
    }
}
