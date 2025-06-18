package com.almacenamiento.backend.model;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Representa a un usuario de tipo Cliente en el sistema.
 * Esta clase hereda de la clase base Usuario.
 * Al usar la estrategia SINGLE_TABLE, esta entidad no necesita su propia
 * anotación @Table, ya que sus datos se guardarán en la tabla 'usuarios'
 * definida en la clase padre.
 */
@Entity
@NoArgsConstructor // Constructor sin argumentos requerido por JPA
public class Cliente extends Usuario {

    /**
     * Constructor para crear una nueva instancia de Cliente.
     * Llama al constructor de la clase padre (Usuario) para inicializar los campos comunes.
     */
    public Cliente(String email, String nombre, String apellido, String contrasenia, Integer telefono, LocalDate fechaNac) {
        super(email, nombre, apellido, contrasenia, telefono, fechaNac);
    }

    /**
     * Implementa el método abstracto de UserDetails para definir los roles y permisos de un Cliente.
     *
     * @return Una colección de autoridades. Para este caso, todos los clientes tienen el rol 'ROLE_USER'.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Por ahora, todos los clientes tienen el rol simple 'ROLE_USER'.
        // Esto es útil para la autorización en Spring Security.
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}