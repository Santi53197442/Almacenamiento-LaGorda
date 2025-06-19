package com.almacenamiento.backend.dto;

import com.almacenamiento.backend.model.Categoria;
import lombok.Data;

@Data
public class ProductoDto {
    private String nombre;
    private int cantidad;
    private String unidadMedida;
    private Categoria categoria;
}