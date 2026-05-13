package com.fashora.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false, length = 10)
    private String pincode;

    @Column(length = 50)
    private String type;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;
}
