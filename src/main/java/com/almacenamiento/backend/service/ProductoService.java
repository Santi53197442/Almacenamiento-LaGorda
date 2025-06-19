package com.almacenamiento.backend.service;

import com.almacenamiento.backend.dto.ProductoDto;
import com.almacenamiento.backend.model.Casa;
import com.almacenamiento.backend.model.Categoria;
import com.almacenamiento.backend.model.Producto;
import com.almacenamiento.backend.model.Usuario;
import com.almacenamiento.backend.repository.ProductoRepository;
import com.almacenamiento.backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public List<Producto> getProductosDeMiCasa() {
        Casa casa = getCasaDelUsuarioAutenticado();
        return productoRepository.findByCasaId(casa.getId());
    }

    @Transactional
    public ProductoDto agregarProducto(ProductoDto productoDto) {
        // --- 1. Obtener usuario y casa (lógica que ya tienes) ---
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + userEmail));

        Casa casaDelUsuario = usuario.getCasa();
        if (casaDelUsuario == null) {
            throw new IllegalStateException("El usuario debe pertenecer a una casa para agregar productos.");
        }

        // --- 2. Crear la ENTIDAD Producto a partir del DTO de la petición ---
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(productoDto.getNombre());
        nuevoProducto.setCantidad(productoDto.getCantidad());
        nuevoProducto.setUnidadMedida(productoDto.getUnidadMedida());

        // Convertimos el String del DTO al tipo Enum que necesita la entidad
        try {
            nuevoProducto.setCategoria(Categoria.valueOf(productoDto.getCategoria().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("La categoría '" + productoDto.getCategoria() + "' no es válida.");
        }

        nuevoProducto.setCasa(casaDelUsuario);

        // --- 3. Guardar la ENTIDAD en la base de datos ---
        Producto productoGuardado = productoRepository.save(nuevoProducto);

        // --- 4. Convertir la ENTIDAD guardada a un DTO para la respuesta ---
        return ProductoDto.builder()
                .id(productoGuardado.getId()) // Incluimos el ID generado
                .nombre(productoGuardado.getNombre())
                .cantidad(productoGuardado.getCantidad())
                .unidadMedida(productoGuardado.getUnidadMedida())
                .categoria(productoGuardado.getCategoria().name()) // Convertimos el Enum de vuelta a String
                .build();
    }

    // Aquí irían los métodos para actualizar y borrar productos...

    private Casa getCasaDelUsuarioAutenticado() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado."));

        if (usuario.getCasa() == null) {
            throw new IllegalStateException("El usuario no pertenece a ninguna casa.");
        }
        return usuario.getCasa();
    }
}