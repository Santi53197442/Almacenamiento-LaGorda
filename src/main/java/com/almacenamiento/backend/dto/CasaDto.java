package com.almacenamiento.backend.dto;

// package com.almacenamiento.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CasaDto {
    private Long id;
    private String nombre;
    private String codigoInvitacion;
}