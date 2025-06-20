package com.almacenamiento.backend.controller;


// en package com.almacenamiento.backend.controller;

import com.almacenamiento.backend.dto.RecetaRequestDto;
import com.almacenamiento.backend.dto.RecetaResponseDto;
import com.almacenamiento.backend.service.RecetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recetas")
@RequiredArgsConstructor
public class RecetaController {
    private final RecetaService recetaService;

    @PostMapping
    public ResponseEntity<RecetaResponseDto> crearReceta(@RequestBody RecetaRequestDto request) {
        return ResponseEntity.ok(recetaService.crearReceta(request));
    }

    // ... endpoints para GET, etc. ...
}