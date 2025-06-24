// Archivo: service/RecetaService.java
package com.almacenamiento.backend.service;

import com.almacenamiento.backend.dto.IngredienteResponseDto;
import com.almacenamiento.backend.dto.ProductoDto;
import com.almacenamiento.backend.dto.RecetaRequestDto;
import com.almacenamiento.backend.dto.RecetaResponseDto;
import com.almacenamiento.backend.model.*;
import com.almacenamiento.backend.repository.ProductoRepository;
import com.almacenamiento.backend.repository.RecetaRepository;
import com.almacenamiento.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecetaService {

    private final RecetaRepository recetaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Crea una nueva receta asociada a la casa del usuario autenticado,
     * incluyendo los ingredientes con sus cantidades específicas.
     * @param request DTO con los datos de la receta a crear.
     * @return DTO de la receta creada.
     */
    @Transactional
    public RecetaResponseDto crearReceta(RecetaRequestDto request) {
        // 1. Obtener el usuario autenticado y su casa.
        Usuario usuario = getUsuarioAutenticado();
        Casa casaDelUsuario = getCasaDelUsuario(usuario);

        // 2. Crear la entidad Receta base (sin los ingredientes aún).
        Receta nuevaReceta = new Receta();
        nuevaReceta.setNombre(request.getNombre());
        nuevaReceta.setImageUrl(request.getImageUrl());
        nuevaReceta.setEnlaceVideo(request.getEnlaceVideo());
        nuevaReceta.setProcedimiento(request.getProcedimiento());
        nuevaReceta.setCasa(casaDelUsuario);

        // 3. 🔥 LÓGICA MODIFICADA: Procesar cada ingrediente del request para crear las entidades de relación.
        Set<RecetaIngrediente> recetaIngredientes = request.getIngredientes().stream()
                .map(ingredienteDto -> {
                    // Buscamos la entidad Producto para asegurar que existe.
                    Producto producto = productoRepository.findById(ingredienteDto.getIdProducto())
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + ingredienteDto.getIdProducto()));

                    // Creamos la nueva entidad de relación (RecetaIngrediente) con la cantidad.
                    // Le pasamos la referencia a 'nuevaReceta' para establecer el vínculo.
                    return new RecetaIngrediente(nuevaReceta, producto, ingredienteDto.getCantidad());
                })
                .collect(Collectors.toSet());

        // 4. Asignamos el conjunto completo de ingredientes relacionados a la receta.
        nuevaReceta.setIngredientes(recetaIngredientes);

        // 5. Guardar la receta. Gracias a `CascadeType.ALL`, JPA también guardará todas las entidades RecetaIngrediente.
        Receta recetaGuardada = recetaRepository.save(nuevaReceta);

        // 6. Convertir la entidad guardada a un DTO de respuesta y devolverla.
        return convertirARecetaResponseDto(recetaGuardada);
    }

    /**
     * Obtiene todas las recetas que pertenecen a la casa del usuario autenticado.
     * @return Una lista de DTOs de las recetas.
     */
    public List<RecetaResponseDto> getRecetasDeMiCasa() {
        try {
            Usuario usuario = getUsuarioAutenticado();
            Casa casaDelUsuario = getCasaDelUsuario(usuario);
            List<Receta> recetas = recetaRepository.findByCasaId(casaDelUsuario.getId());

            return recetas.stream()
                    .map(this::convertirARecetaResponseDto)
                    .collect(Collectors.toList());

        } catch (IllegalStateException e) {
            return Collections.emptyList();
        }
    }

    // --- MÉTODOS PRIVADOS DE AYUDA ---

    /**
     * Convierte una entidad Receta a su correspondiente DTO de respuesta,
     * incluyendo la nueva estructura de ingredientes con cantidad.
     */
    private RecetaResponseDto convertirARecetaResponseDto(Receta receta) {
        // Mapeamos cada RecetaIngrediente a un IngredienteResponseDto
        List<IngredienteResponseDto> ingredienteDtos = receta.getIngredientes().stream()
                .map(recetaIngrediente -> IngredienteResponseDto.builder()
                        .producto(convertirAProductoDto(recetaIngrediente.getProducto()))
                        .cantidad(recetaIngrediente.getCantidad())
                        .build()
                ).collect(Collectors.toList());

        return RecetaResponseDto.builder()
                .id(receta.getId())
                .nombre(receta.getNombre())
                .imageUrl(receta.getImageUrl())
                .enlaceVideo(receta.getEnlaceVideo())
                .procedimiento(receta.getProcedimiento())
                .ingredientes(ingredienteDtos)
                .build();
    }

    /**
     * Convierte una entidad Producto a un ProductoDto.
     */
    private ProductoDto convertirAProductoDto(Producto producto) {
        return ProductoDto.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .cantidad(producto.getCantidad())
                .unidadMedida(producto.getUnidadMedida())
                .categoria(producto.getCategoria().name())
                .build();
    }

    /**
     * Obtiene la entidad Usuario del usuario actualmente autenticado.
     */
    private Usuario getUsuarioAutenticado() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado en la base de datos."));
    }

    /**
     * Obtiene la Casa de un usuario y lanza una excepción si no tiene una.
     */
    private Casa getCasaDelUsuario(Usuario usuario) {
        if (usuario.getCasa() == null) {
            throw new IllegalStateException("El usuario debe pertenecer a una casa para realizar esta acción.");
        }
        return usuario.getCasa();
    }
}