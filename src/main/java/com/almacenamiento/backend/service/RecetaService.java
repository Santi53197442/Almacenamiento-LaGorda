package com.almacenamiento.backend.service;

import com.almacenamiento.backend.dto.ProductoDto;
import com.almacenamiento.backend.dto.RecetaRequestDto;
import com.almacenamiento.backend.dto.RecetaResponseDto;
import com.almacenamiento.backend.model.Casa;
import com.almacenamiento.backend.model.Producto;
import com.almacenamiento.backend.model.Receta;
import com.almacenamiento.backend.model.Usuario;
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
     * Crea una nueva receta asociada a la casa del usuario autenticado.
     * @param request DTO con los datos de la receta a crear.
     * @return DTO de la receta creada.
     */
    @Transactional
    public RecetaResponseDto crearReceta(RecetaRequestDto request) {
        // 1. Obtener el usuario autenticado y su casa.
        Usuario usuario = getUsuarioAutenticado();
        Casa casaDelUsuario = getCasaDelUsuario(usuario);

        // 2. Buscar las entidades Producto por los IDs proporcionados.
        // Esto asegura que solo se asocien productos que realmente existen en la BD.
        Set<Producto> ingredientes = new HashSet<>(productoRepository.findAllById(request.getIngredienteIds()));

        // 3. Crear y poblar la nueva entidad Receta.
        Receta nuevaReceta = new Receta();
        nuevaReceta.setNombre(request.getNombre());
        nuevaReceta.setImageUrl(request.getImageUrl());
        nuevaReceta.setEnlaceVideo(request.getEnlaceVideo());
        nuevaReceta.setProcedimiento(request.getProcedimiento());
        nuevaReceta.setCasa(casaDelUsuario);
        nuevaReceta.setIngredientes(ingredientes);

        // 4. Guardar la nueva receta en la base de datos.
        Receta recetaGuardada = recetaRepository.save(nuevaReceta);

        // 5. Convertir la entidad guardada a un DTO de respuesta y devolverla.
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

            // Convertir la lista de Entidades Receta a una lista de DTOs de respuesta.
            return recetas.stream()
                    .map(this::convertirARecetaResponseDto)
                    .collect(Collectors.toList());

        } catch (IllegalStateException e) {
            // Si el usuario no tiene casa, devuelve una lista vacía.
            return Collections.emptyList();
        }
    }

    // --- MÉTODOS PRIVADOS DE AYUDA ---

    /**
     * Convierte una entidad Receta a su correspondiente DTO de respuesta.
     */
    private RecetaResponseDto convertirARecetaResponseDto(Receta receta) {
        // Convierte la lista de entidades Producto a una lista de ProductoDto.
        List<ProductoDto> ingredienteDtos = receta.getIngredientes().stream()
                .map(this::convertirAProductoDto) // Reutiliza el método de conversión de Producto.
                .collect(Collectors.toList());

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
     * (Este método puede estar duplicado si también lo tienes en ProductoService.
     * En un proyecto más grande, se podría mover a una clase 'Mapper' de utilidad).
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
            throw new IllegalStateException("El usuario debe pertenecer a ninguna casa para realizar esta acción.");
        }
        return usuario.getCasa();
    }
}