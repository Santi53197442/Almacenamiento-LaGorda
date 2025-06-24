// Archivo: model/Receta.java
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

    // 🔥 CAMBIO CRUCIAL: Se reemplaza @ManyToMany por esta relación @OneToMany
    // `mappedBy = "receta"`: Indica que la entidad RecetaIngrediente gestiona la relación.
    // `cascade = CascadeType.ALL`: Al guardar/actualizar/eliminar una Receta, se aplica la misma operación a sus RecetaIngrediente.
    // `orphanRemoval = true`: Si un RecetaIngrediente es removido de este Set, se elimina de la base de datos.
    @OneToMany(
            mappedBy = "receta",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private Set<RecetaIngrediente> ingredientes = new HashSet<>();
}