// en: dto/IngredienteRequestDto.java
package com.almacenamiento.backend.dto;

import lombok.Data;

@Data
public class IngredienteRequestDto {
    private Long idProducto;
    private String cantidad;
}