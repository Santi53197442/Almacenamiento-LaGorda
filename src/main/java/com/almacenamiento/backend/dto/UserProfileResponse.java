package com.almacenamiento.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private String email;
    private String nombre;
    private String apellido;
    // Usaremos un DTO para la casa para no exponer la entidad completa
    private CasaDto casa;
}