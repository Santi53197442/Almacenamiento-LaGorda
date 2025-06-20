package com.almacenamiento.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecetaRequestDto {
    private String nombre;
    private String imageUrl;
    private String enlaceVideo;
    private String procedimiento;
    private List<Long> ingredienteIds; // La app solo necesita enviar los IDs de los productos
}