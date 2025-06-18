package com.almacenamiento.backend.service;


import com.almacenamiento.backend.dto.AuthResponse.AuthResponse;
import com.almacenamiento.backend.dto.AuthResponse.LoginRequest;
import com.almacenamiento.backend.dto.AuthResponse.RegisterRequest;
import com.almacenamiento.backend.model.Cliente;
import com.almacenamiento.backend.repository.ClienteRepository;
import com.almacenamiento.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Verificar si el email ya existe
        if (clienteRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está en uso.");
        }

        var cliente = new Cliente(
                request.getEmail(),
                request.getNombre(),
                request.getApellido(),
                passwordEncoder.encode(request.getContrasenia()),
                request.getTelefono(),
                request.getFechaNac()
        );

        clienteRepository.save(cliente);

        var jwtToken = jwtService.generateToken(cliente);
        return AuthResponse.builder().token(jwtToken).build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getContrasenia()
                )
        );

        // Si la autenticación es exitosa, buscamos al usuario para generar el token
        var user = clienteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder().token(jwtToken).build();
    }
}