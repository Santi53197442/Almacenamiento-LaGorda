package com.almacenamiento.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data // Anotación de Lombok para getters, setters, toString, equals, hashCode.
@Builder // Para construir objetos de forma fluida (Ej: Usuario.builder().email(...).build())
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails { // Implementa UserDetails para la seguridad

    // --- CAMPOS DE LA ENTIDAD ---

    @Id
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, length = 255)
    private String contrasenia; // El nombre del campo en la clase

    @Column(nullable = false)
    private Integer telefono;

    @Column(nullable = false)
    private LocalDate fechaNac;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // CAMPOS PARA RECUPERACIÓN DE CONTRASEÑA (si los necesitas en el futuro)
    @Column(name = "reset_password_token", length = 100)
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expiry_date")
    private LocalDateTime resetPasswordTokenExpiryDate;


    // --- 🔥 NUEVA RELACIÓN CON CASA 🔥 ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casa_id") // Esta será la columna de clave foránea en la tabla 'usuarios'
    @JsonIgnoreProperties({"usuarios", "productos"}) // Evita bucles infinitos al convertir a JSON
    private Casa casa;


    // Constructor público para facilitar la creación desde el servicio de registro
    public Usuario(String email, String nombre, String apellido, String contrasenia, Integer telefono, LocalDate fechaNac) {
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.contrasenia = contrasenia;
        this.telefono = telefono;
        this.fechaNac = fechaNac;
    }


    // --- MÉTODOS REQUERIDOS POR LA INTERFAZ UserDetails ---

    /**
     * Devuelve los roles/permisos del usuario. Para esta app, todos son "USER".
     */
    // En el método getAuthorities()
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // AHORA USAMOS LA CONVENCIÓN DE ROLES DE SPRING
        return List.of(new SimpleGrantedAuthority("ROLE_USER")); // <-- LÍNEA CORREGIDA
    }

    /**
     * Devuelve la contraseña del usuario. Spring Security la usará para la autenticación.
     */
    @Override
    public String getPassword() {
        // Importante que devuelva el campo que contiene la contraseña hasheada
        return this.contrasenia;
    }

    /**
     * Devuelve el identificador único del usuario. Spring Security lo usará como "username".
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    // Los siguientes métodos de UserDetails se dejan en 'true' por simplicidad.
    // Podrían usarse en el futuro para implementar lógicas de bloqueo de cuentas, etc.

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}