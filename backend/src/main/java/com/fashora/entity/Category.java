package com.fashora.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 100)
    private String slug;

    @Column(length = 10)
    private String emoji;

    @Column(name = "product_count")
    @Builder.Default
    private Integer productCount = 0;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
