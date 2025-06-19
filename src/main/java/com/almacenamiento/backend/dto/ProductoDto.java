package com.almacenamiento.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder // Nos permite construir el objeto de forma más legible
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDto {

    // El ID es opcional en la petición, pero esencial en la respuesta.
    private Long id;

    private String nombre;
    private int cantidad;
    private String unidadMedida;

    // Usamos String en el DTO para que sea más simple.
    // La capa de servicio se encargará de convertirlo al Enum Categoria.
    private String categoria;
}