package com.almacenamiento.backend.controller;

import com.almacenamiento.backend.dto.CasaDto;
import com.almacenamiento.backend.dto.JoinCasaDto;
import com.almacenamiento.backend.model.Casa;
import com.almacenamiento.backend.service.CasaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/casas")
@RequiredArgsConstructor
public class CasaController {

    private final CasaService casaService;

    @PostMapping
    public ResponseEntity<Casa> crearCasa(@RequestBody CasaDto casaDto) {
        Casa nuevaCasa = casaService.crearCasa(casaDto.getNombre());
        return ResponseEntity.ok(nuevaCasa);
    }

    @PostMapping("/join")
    public ResponseEntity<Casa> unirseACasa(@RequestBody JoinCasaDto joinDto) {
        Casa casa = casaService.unirseACasa(joinDto.getCodigoInvitacion());
        return ResponseEntity.ok(casa);
    }
}