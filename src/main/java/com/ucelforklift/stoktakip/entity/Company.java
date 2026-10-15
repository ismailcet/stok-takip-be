package com.ucelforklift.stoktakip.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name must not be blank")
    @Column(nullable = false, length = 150)
    private String name;

    // Eski kayitlarda bos olabilir, bu yuzden DB'de nullable.
    @Column(length = 500)
    private String address;

    @Column(name = "tax_office", length = 100)
    private String taxOffice;

    @Column(name = "tax_number", length = 11)
    private String taxNumber;

    @Column(length = 20)
    private String phone;

    // Gizli firma yeni bakim kaydi eklerken secilemez; eski kayitlari listelenmeye devam eder.
    @Column(nullable = false)
    private boolean hidden = false;
}