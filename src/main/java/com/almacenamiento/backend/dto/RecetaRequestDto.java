// en: dto/RecetaRequestDto.java
package com.almacenamiento.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecetaRequestDto {
    private String nombre;
    private String imageUrl;
    private String enlaceVideo;
    private String procedimiento;
    // 🔥 CAMBIO: de List<Long> a List<IngredienteRequestDto>
    private List<IngredienteRequestDto> ingredientes;
}