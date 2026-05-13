package com.fashora.service;

import com.fashora.dto.ProductDto;
import com.fashora.exception.ResourceNotFoundException;
import com.fashora.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductDto> getAll(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findByIsActiveTrue(pageable).map(ProductDto::from);
    }

    public Page<ProductDto> getByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.findByCategoryIgnoreCaseAndIsActiveTrue(category, pageable).map(ProductDto::from);
    }

    public Page<ProductDto> search(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.searchProducts(query, pageable).map(ProductDto::from);
    }

    public ProductDto getById(Long id) {
        return productRepository.findById(id)
                .map(ProductDto::from)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public List<ProductDto> getTrending() {
        return productRepository.findTop8ByIsActiveTrueOrderByRatingDesc()
                .stream().map(ProductDto::from).collect(Collectors.toList());
    }

    public List<ProductDto> getNewArrivals() {
        return productRepository.findTop8ByIsActiveTrueOrderByCreatedAtDesc()
                .stream().map(ProductDto::from).collect(Collectors.toList());
    }

    public List<ProductDto> getSaleProducts() {
        return productRepository.findTop8ByBadgeAndIsActiveTrueOrderByCreatedAtDesc("sale")
                .stream().map(ProductDto::from).collect(Collectors.toList());
    }

    public Page<ProductDto> getByBadge(String badge, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByBadgeAndIsActiveTrue(badge, pageable).map(ProductDto::from);
    }
}
