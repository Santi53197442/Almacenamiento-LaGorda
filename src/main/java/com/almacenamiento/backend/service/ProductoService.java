package com.almacenamiento.backend.service;

import com.almacenamiento.backend.dto.ProductoDto;
import com.almacenamiento.backend.model.Casa;
import com.almacenamiento.backend.model.Producto;
import com.almacenamiento.backend.model.Usuario;
import com.almacenamiento.backend.repository.ProductoRepository;
import com.almacenamiento.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public Producto agregarProducto(ProductoDto productoDto) {
        Casa casa = getCasaDelUsuarioAutenticado();

        Producto nuevoProducto = Producto.builder()
                .nombre(productoDto.getNombre())
                .cantidad(productoDto.getCantidad())
                .unidadMedida(productoDto.getUnidadMedida())
                .categoria(productoDto.getCategoria())
                .casa(casa)
                .build();

        return productoRepository.save(nuevoProducto);
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