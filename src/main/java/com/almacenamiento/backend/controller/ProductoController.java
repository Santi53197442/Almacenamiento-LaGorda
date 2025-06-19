package com.almacenamiento.backend.controller;

import com.almacenamiento.backend.dto.ProductoDto;
import com.almacenamiento.backend.dto.StockUpdateRequestDto; // <-- Asegúrate de importar este DTO
import com.almacenamiento.backend.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; // <-- Asegúrate de importar List

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // --- Endpoint para CREAR un nuevo producto ---
    @PostMapping
    public ResponseEntity<ProductoDto> agregarNuevoProducto(@RequestBody ProductoDto productoDto) {
        // 🔥 ESTA ES LA LÍNEA QUE CORREGIMOS 🔥
        // Llamamos al método con el nuevo nombre: agregarNuevoProducto
        ProductoDto productoCreado = productoService.agregarNuevoProducto(productoDto);
        return ResponseEntity.ok(productoCreado);
    }

    // --- Endpoint para AÑADIR stock a un producto existente ---
    @PutMapping("/{productoId}/add-stock")
    public ResponseEntity<ProductoDto> agregarStock(
            @PathVariable Long productoId,
            @RequestBody StockUpdateRequestDto request) {

        ProductoDto productoActualizado = productoService.actualizarStock(productoId, request.getCantidad());
        return ResponseEntity.ok(productoActualizado);
    }

    // --- Endpoint para RESTAR stock de un producto existente ---
    @PutMapping("/{productoId}/subtract-stock")
    public ResponseEntity<ProductoDto> restarStock(
            @PathVariable Long productoId,
            @RequestBody StockUpdateRequestDto request) {

        // Enviamos la cantidad como un número negativo para que el servicio la reste.
        ProductoDto productoActualizado = productoService.actualizarStock(productoId, -request.getCantidad());
        return ResponseEntity.ok(productoActualizado);
    }

    // --- Endpoint para OBTENER todos los productos de la casa del usuario ---
    @GetMapping
    public ResponseEntity<List<ProductoDto>> getProductosDeMiCasa() {
        List<ProductoDto> productos = productoService.getProductosDeMiCasa();
        return ResponseEntity.ok(productos);
    }
}