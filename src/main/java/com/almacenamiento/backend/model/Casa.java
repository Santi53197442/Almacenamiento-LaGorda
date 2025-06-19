package com.almacenamiento.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "casas")
public class Casa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Nombre de casa único puede ser buena idea
    private String nombre;

    // Generaremos un código simple para unirse
    @Column(nullable = false, unique = true)
    private String codigoInvitacion;

    @OneToMany(mappedBy = "casa", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("casa") // Evita bucles infinitos en JSON
    private List<Usuario> usuarios;

    @OneToMany(mappedBy = "casa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("casa") // Evita bucles infinitos en JSON
    private List<Producto> productos;
}