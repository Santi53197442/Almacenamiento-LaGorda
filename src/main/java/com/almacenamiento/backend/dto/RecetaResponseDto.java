package com.almacenamiento.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class RecetaResponseDto {
    private Long id;
    private String nombre;
    private String imageUrl;
    private String enlaceVideo;
    private String procedimiento;
    // 👇 CAMBIO AQUÍ: Ahora coincide con lo que creas en el servicio
    private List<IngredienteResponseDto> ingredientes;
}