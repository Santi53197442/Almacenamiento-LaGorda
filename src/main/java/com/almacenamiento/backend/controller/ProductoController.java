package com.almacenamiento.backend.controller;

import com.almacenamiento.backend.dto.ProductoDto;
import com.almacenamiento.backend.model.Producto;
import com.almacenamiento.backend.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> getProductosDeMiCasa() {
        return ResponseEntity.ok(productoService.getProductosDeMiCasa());
    }

    @PostMapping
    public ResponseEntity<ProductoDto> agregarProducto(@RequestBody ProductoDto productoDto) {
        ProductoDto productoCreado = productoService.agregarProducto(productoDto);
        return ResponseEntity.ok(productoCreado);
    }
}
