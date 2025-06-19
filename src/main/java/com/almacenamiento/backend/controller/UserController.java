package com.almacenamiento.backend.controller;

import com.almacenamiento.backend.dto.UserProfileResponse;
import com.almacenamiento.backend.service.AuthService; // o un UserService si lo tienes separado
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user") // <-- ¡La ruta base que espera tu app!
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @GetMapping("/me") // <-- ¡El endpoint exacto que espera tu app!
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        // Llamamos al nuevo método del servicio
        UserProfileResponse userProfile = authService.getUserProfile();
        return ResponseEntity.ok(userProfile);
    }
}