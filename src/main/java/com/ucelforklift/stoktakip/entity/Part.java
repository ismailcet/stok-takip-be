package com.ucelforklift.stoktakip.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "parts")
@Getter
@Setter
@NoArgsConstructor
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Part name must not be blank")
    @Column(nullable = false, length = 150)
    private String name;

    @Column(unique = true, length = 100)
    private String barcode;

    @Column(name = "product_code", unique = true, length = 100)
    private String productCode;

    @Column(length = 100)
    private String brand;

    @Column(length = 100)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabinet_id", nullable = false)
    private Cabinet cabinet;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;
}