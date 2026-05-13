package com.fashora.controller;

import com.fashora.dto.ApiResponse;
import com.fashora.entity.Category;
import com.fashora.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(categoryRepository.findByIsActiveTrueOrderByName()));
    }
}
