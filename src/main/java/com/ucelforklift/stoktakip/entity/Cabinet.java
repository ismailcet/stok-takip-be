package com.ucelforklift.stoktakip.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cabinets")
@Getter
@Setter
@NoArgsConstructor
public class Cabinet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Cabinet name must not be blank")
    @Column(nullable = false, unique = true, length = 50)
    private String name;
}