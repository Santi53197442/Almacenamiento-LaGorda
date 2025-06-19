package com.almacenamiento.backend.service; // Asegúrate de que el paquete sea el correcto

import com.almacenamiento.backend.dto.*; // <-- NUEVO (Importa todos los DTOs, incluido UserProfileResponse)
import com.almacenamiento.backend.model.Usuario;
import com.almacenamiento.backend.repository.UsuarioRepository;
import com.almacenamiento.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder; // <-- NUEVO
import org.springframework.security.core.userdetails.UsernameNotFoundException; // <-- NUEVO
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    /**
     * Registra un nuevo usuario en el sistema.
     * ... (tu método register() se queda igual, no hay que cambiarlo)
     */
    public AuthResponse register(RegisterRequest request) {
        logger.info("Iniciando proceso de registro para el email: {}", request.getEmail());
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("Intento de registro con email ya existente: {}", request.getEmail());
            throw new IllegalArgumentException("El email ya está en uso.");
        }
        var usuario = new Usuario(
                request.getEmail(),
                request.getNombre(),
                request.getApellido(),
                passwordEncoder.encode(request.getContrasenia()),
                request.getTelefono(),
                request.getFechaNac()
        );
        usuarioRepository.save(usuario);
        logger.info("Usuario guardado en la base de datos: {}", request.getEmail());
        var jwtToken = jwtService.generateToken(usuario);
        return AuthResponse.builder().token(jwtToken).build();
    }

    /**
     * Autentica a un usuario existente.
     * ... (tu método login() se queda igual, no hay que cambiarlo)
     */
    public AuthResponse login(LoginRequest request) {
        logger.info("Iniciando intento de login para el usuario: {}", request.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getContrasenia()
                    )
            );
            logger.info("Autenticación exitosa para: {}", request.getEmail());
        } catch (Exception e) {
            logger.error("Fallo en la autenticación para {}: {}", request.getEmail(), e.getMessage());
            throw e;
        }
        logger.info("Buscando usuario en la BD para generar token: {}", request.getEmail());
        var usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Error inesperado: Usuario no encontrado después de una autenticación exitosa."));
        var jwtToken = jwtService.generateToken(usuario);
        logger.info("Login completado y token generado para: {}", request.getEmail());
        return AuthResponse.builder().token(jwtToken).build();
    }

    // =========================================================================
    //               AQUÍ EMPIEZA EL CÓDIGO NUEVO QUE DEBES AÑADIR
    // =========================================================================

    /**
     * Obtiene el perfil del usuario actualmente autenticado.
     * Utiliza el contexto de seguridad para identificar al usuario a través del token JWT.
     *
     * @return un DTO con la información del perfil del usuario.
     * @throws UsernameNotFoundException si el usuario del token no se encuentra en la BD.
     */
    public UserProfileResponse getUserProfile() {
        logger.info("Iniciando obtención de perfil de usuario.");

        // 1. Obtener el email del usuario autenticado desde el contexto de seguridad de Spring
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Email del usuario autenticado: {}", userEmail);

        // 2. Buscar al usuario en la base de datos usando el email
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado en la BD con email del token: {}", userEmail);
                    return new UsernameNotFoundException("Usuario no encontrado con email: " + userEmail);
                });

        logger.debug("Usuario encontrado en la BD: {}", usuario.getEmail());

        // 3. Mapear la información de la casa del usuario a un DTO.
        //    Esto evita exponer la entidad completa de la Casa y sus relaciones.
        CasaDto casaDto = null;
        if (usuario.getCasa() != null) {
            logger.debug("El usuario pertenece a la casa: {}", usuario.getCasa().getNombre());
            casaDto = CasaDto.builder()
                    .id(usuario.getCasa().getId())
                    .nombre(usuario.getCasa().getNombre())
                    .codigoInvitacion(usuario.getCasa().getCodigoInvitacion())
                    .build();
        } else {
            logger.debug("El usuario no tiene una casa asignada.");
        }

        // 4. Construir y devolver la respuesta con los datos del perfil
        UserProfileResponse profileResponse = UserProfileResponse.builder()
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .casa(casaDto) // Será null si el usuario no tiene casa, que es lo que espera la app.
                .build();

        logger.info("Perfil de usuario generado exitosamente para: {}", userEmail);
        return profileResponse;
    }
}