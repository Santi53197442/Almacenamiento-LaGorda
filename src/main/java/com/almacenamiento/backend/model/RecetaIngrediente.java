// en: model/RecetaIngrediente.java
package com.almacenamiento.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "receta_ingredientes")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"receta", "producto"}) // Evita recursión en equals/hashCode
public class RecetaIngrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id")
    private Receta receta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(nullable = false)
    private String cantidad; // "200 gr", "1 taza", etc.

    // Constructor útil
    public RecetaIngrediente(Receta receta, Producto producto, String cantidad) {
        this.receta = receta;
        this.producto = producto;
        this.cantidad = cantidad;
    }
}