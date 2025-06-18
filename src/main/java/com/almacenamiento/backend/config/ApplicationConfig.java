
package com.almacenamiento.backend.config;

// Imports necesarios
import com.almacenamiento.backend.repository.ClienteRepository; // Asegúrate de que el import sea el de tu repositorio
import com.almacenamiento.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // <--- ANOTACIÓN CRÍTICA #1
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UsuarioRepository usuarioRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        // La lógica es la misma, pero ahora usa el nuevo repositorio
        return username -> usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + username));
    }

    @Bean // <--- ANOTACIÓN CRÍTICA #3 (ESTA ES LA QUE CAUSA EL ERROR)
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService()); // Usa el bean de arriba
        authProvider.setPasswordEncoder(passwordEncoder()); // Usa el bean de abajo
        return authProvider;
    }

    @Bean // <--- ANOTACIÓN CRÍTICA #4
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean // <--- ANOTACIÓN CRÍTICA #5
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}