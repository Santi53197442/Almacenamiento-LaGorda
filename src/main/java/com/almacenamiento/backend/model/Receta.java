package com.almacenamiento.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "recetas")
@Data
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String imageUrl; // URL de la imagen hosteada en Cloudinary

    private String enlaceVideo; // Opcional

    @Lob // Para textos largos
    @Column(columnDefinition = "TEXT")
    private String procedimiento; // Opcional

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casa_id", nullable = false)
    private Casa casa;

    // Relación Muchos-a-Muchos con Producto
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "receta_ingredientes",
            joinColumns = @JoinColumn(name = "receta_id"),
            inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private Set<Producto> ingredientes = new HashSet<>();
}