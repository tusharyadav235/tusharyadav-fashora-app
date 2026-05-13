package com.fashora.dto;

import com.fashora.entity.Product;
import lombok.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private String brand;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String category;
    private String subcategory;
    private String badge;
    private String imageEmoji;
    private String imageUrl;
    private Integer stock;
    private BigDecimal rating;
    private Integer reviewCount;
    private Boolean isActive;
    private List<String> sizes;
    private List<String> colors;
    private Integer discountPercent;

    public static ProductDto from(Product p) {
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setBrand(p.getBrand());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setOriginalPrice(p.getOriginalPrice());
        dto.setCategory(p.getCategory());
        dto.setSubcategory(p.getSubcategory());
        dto.setBadge(p.getBadge());
        dto.setImageEmoji(p.getImageEmoji());
        dto.setImageUrl(p.getImageUrl());
        dto.setStock(p.getStock());
        dto.setRating(p.getRating());
        dto.setReviewCount(p.getReviewCount());
        dto.setIsActive(p.getIsActive());
        dto.setSizes(p.getSizes());
        dto.setColors(p.getColors());
        if (p.getOriginalPrice() != null && p.getOriginalPrice().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diff = p.getOriginalPrice().subtract(p.getPrice());
            BigDecimal pct = diff.divide(p.getOriginalPrice(), 2, RoundingMode.HALF_UP)
                               .multiply(BigDecimal.valueOf(100));
            dto.setDiscountPercent(pct.intValue());
        } else {
            dto.setDiscountPercent(0);
        }
        return dto;
    }
}
