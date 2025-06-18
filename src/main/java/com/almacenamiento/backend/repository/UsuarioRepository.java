package com.almacenamiento.backend.repository;

import com.almacenamiento.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> { // <-- Ahora es de Usuario, String

    Optional<Usuario> findByEmail(String email);

}