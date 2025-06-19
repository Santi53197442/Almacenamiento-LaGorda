package com.almacenamiento.backend.service;

import com.almacenamiento.backend.dto.ProductoDto;
import com.almacenamiento.backend.model.Casa;
import com.almacenamiento.backend.model.Categoria;
import com.almacenamiento.backend.model.Producto;
import com.almacenamiento.backend.model.Usuario;
import com.almacenamiento.backend.repository.ProductoRepository;
import com.almacenamiento.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- Es mejor usar el de Spring

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Crea un nuevo producto en la base de datos asociado a la casa del usuario autenticado.
     */
    @Transactional
    public ProductoDto agregarNuevoProducto(ProductoDto productoDto) {
        // --- 1. Obtener usuario y casa ---
        Usuario usuario = getUsuarioAutenticado();
        Casa casaDelUsuario = getCasaDelUsuario(usuario);

        // --- 2. Crear la ENTIDAD Producto a partir del DTO ---
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(productoDto.getNombre());
        nuevoProducto.setCantidad(productoDto.getCantidad());
        nuevoProducto.setUnidadMedida(productoDto.getUnidadMedida());

        try {
            nuevoProducto.setCategoria(Categoria.valueOf(productoDto.getCategoria().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("La categoría '" + productoDto.getCategoria() + "' no es válida.");
        }
        nuevoProducto.setCasa(casaDelUsuario);

        // --- 3. Guardar y convertir a DTO para la respuesta ---
        Producto productoGuardado = productoRepository.save(nuevoProducto);
        return convertirAProductoDto(productoGuardado);
    }

    /**
     * Actualiza el stock de un producto existente. Sirve para sumar (cantidad positiva) o restar (cantidad negativa).
     */
    @Transactional
    public ProductoDto actualizarStock(Long productoId, int cantidadAModificar) {
        // --- 1. Obtener usuario y validar que el producto le pertenece ---
        Usuario usuario = getUsuarioAutenticado();
        Casa casaDelUsuario = getCasaDelUsuario(usuario);

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));

        // Validación de seguridad CRÍTICA
        if (!producto.getCasa().getId().equals(casaDelUsuario.getId())) {
            throw new SecurityException("Acceso denegado. No tienes permiso para modificar este producto.");
        }

        // --- 2. Actualizar la cantidad ---
        int nuevaCantidad = producto.getCantidad() + cantidadAModificar;
        if (nuevaCantidad < 0) {
            throw new IllegalArgumentException("No se puede restar más cantidad de la existente. Stock actual: " + producto.getCantidad());
        }

        producto.setCantidad(nuevaCantidad);
        Producto productoActualizado = productoRepository.save(producto);

        // --- 3. Devolver DTO actualizado ---
        return convertirAProductoDto(productoActualizado);
    }

    /**
     * Obtiene todos los productos de la casa del usuario autenticado.
     * @return Una lista de ProductoDto.
     */
    public List<ProductoDto> getProductosDeMiCasa() {
        try {
            Usuario usuario = getUsuarioAutenticado();
            Casa casaDelUsuario = getCasaDelUsuario(usuario);
            List<Producto> productos = productoRepository.findByCasaId(casaDelUsuario.getId());

            // Convertimos la lista de Entidades a una lista de DTOs
            return productos.stream()
                    .map(this::convertirAProductoDto)
                    .collect(Collectors.toList());
        } catch (IllegalStateException e) {
            // Si el usuario no tiene casa, devolvemos una lista vacía.
            return Collections.emptyList();
        }
    }

    // --- MÉTODOS PRIVADOS DE AYUDA ---

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
            throw new IllegalStateException("El usuario no pertenece a ninguna casa.");
        }
        return usuario.getCasa();
    }
}