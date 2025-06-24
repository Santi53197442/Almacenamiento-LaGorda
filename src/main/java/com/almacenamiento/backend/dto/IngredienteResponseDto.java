// Archivo: dto/IngredienteResponseDto.java
package com.almacenamiento.backend.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object para representar un ingrediente dentro de una receta
 * cuando se envía una respuesta desde el servidor al cliente.
 *
 * Contiene el producto en sí (representado por ProductoDto) y la cantidad
 * específica utilizada en la receta.
 */
@Data
@Builder // El patrón Builder es muy útil para construir estos DTOs de forma limpia.
public class IngredienteResponseDto {

    /**
     * El producto que se utiliza como ingrediente.
     * Contiene toda la información del producto (ID, nombre, categoría, etc.).
     */
    private ProductoDto producto;

    /**
     * La cantidad de este producto que se necesita para la receta.
     * (e.g., "200 gr", "1 taza", "3 unidades").
     */
    private String cantidad;

}