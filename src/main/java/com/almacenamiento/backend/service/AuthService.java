package com.almacenamiento.backend.service; // Asegúrate de que el paquete sea el correcto


import com.almacenamiento.backend.dto.AuthResponse.AuthResponse;
import com.almacenamiento.backend.dto.AuthResponse.LoginRequest;
import com.almacenamiento.backend.dto.AuthResponse.RegisterRequest;
import com.almacenamiento.backend.model.Usuario;
import com.almacenamiento.backend.repository.UsuarioRepository;
import com.almacenamiento.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    // Dependencias necesarias para el servicio de autenticación
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Logger para depuración
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request DTO con los datos del usuario a registrar.
     * @return una respuesta con el token JWT para el nuevo usuario.
     * @throws IllegalArgumentException si el email ya está en uso.
     */
    public AuthResponse register(RegisterRequest request) {
        logger.info("Iniciando proceso de registro para el email: {}", request.getEmail());

        // 1. Verificar si el email ya existe en la base de datos
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("Intento de registro con email ya existente: {}", request.getEmail());
            throw new IllegalArgumentException("El email ya está en uso.");
        }

        // 2. Crear una nueva instancia de la entidad Usuario
        var usuario = new Usuario(
                request.getEmail(),
                request.getNombre(),
                request.getApellido(),
                passwordEncoder.encode(request.getContrasenia()), // Importante: Codificar la contraseña
                request.getTelefono(),
                request.getFechaNac()
        );
        logger.debug("Nuevo objeto Usuario creado para: {}", request.getEmail());

        // 3. Guardar el nuevo usuario en la base de datos
        usuarioRepository.save(usuario);
        logger.info("Usuario guardado en la base de datos: {}", request.getEmail());

        // 4. Generar un token JWT para el usuario recién registrado
        var jwtToken = jwtService.generateToken(usuario);
        logger.info("Token JWT generado para el nuevo usuario.");

        // 5. Devolver la respuesta con el token
        return AuthResponse.builder().token(jwtToken).build();
    }

    /**
     * Autentica a un usuario existente.
     *
     * @param request DTO con el email y la contraseña del usuario.
     * @return una respuesta con el token JWT si la autenticación es exitosa.
     * @throws org.springframework.security.core.AuthenticationException si las credenciales son inválidas.
     */
    public AuthResponse login(LoginRequest request) {
        logger.info("Iniciando intento de login para el usuario: {}", request.getEmail());

        // 1. Autenticar con Spring Security. Esto valida email y contraseña.
        // Si las credenciales son incorrectas, lanzará una AuthenticationException.
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
            throw e; // Relanzar la excepción para que Spring Security la maneje (devuelve 401/403)
        }

        // 2. Si la autenticación fue exitosa, buscar al usuario en la BD para obtener sus datos completos
        logger.info("Buscando usuario en la BD para generar token: {}", request.getEmail());
        var usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Error inesperado: Usuario no encontrado después de una autenticación exitosa."));

        // 3. Generar y devolver el token JWT
        var jwtToken = jwtService.generateToken(usuario);
        logger.info("Login completado y token generado para: {}", request.getEmail());

        return AuthResponse.builder().token(jwtToken).build();
    }
}