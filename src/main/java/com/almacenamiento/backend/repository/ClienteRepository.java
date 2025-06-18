package com.almacenamiento.backend.repository;


import com.almacenamiento.backend.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// El ID de Cliente es el email (String)
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {

    // Spring Data JPA crea automáticamente la consulta por el nombre del método
    Optional<Cliente> findByEmail(String email);

}