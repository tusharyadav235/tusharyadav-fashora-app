package com.fashora.repository;

import com.fashora.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByIsActiveTrue(Pageable pageable);

    Page<Product> findByCategoryIgnoreCaseAndIsActiveTrue(String category, Pageable pageable);

    Page<Product> findByBadgeAndIsActiveTrue(String badge, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> searchProducts(@Param("query") String query, Pageable pageable);

    List<Product> findTop8ByIsActiveTrueOrderByRatingDesc();

    List<Product> findTop8ByIsActiveTrueOrderByCreatedAtDesc();

    List<Product> findTop8ByBadgeAndIsActiveTrueOrderByCreatedAtDesc(String badge);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.isActive = true")
    List<String> findDistinctCategories();

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice,
                                   @Param("maxPrice") java.math.BigDecimal maxPrice,
                                   Pageable pageable);
}
