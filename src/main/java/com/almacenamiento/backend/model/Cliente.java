package com.almacenamiento.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "clientes")
@NoArgsConstructor
public class Cliente extends Usuario {

    // Constructor que llama al de la clase padre
    public Cliente(String email, String nombre, String apellido, String contrasenia, Integer telefono, LocalDate fechaNac) {
        super(email, nombre, apellido, contrasenia, telefono, fechaNac);
    }

    // Implementación del método abstracto para definir los roles/permisos del Cliente
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Por ahora, todos los clientes tienen el rol 'ROLE_USER'
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}