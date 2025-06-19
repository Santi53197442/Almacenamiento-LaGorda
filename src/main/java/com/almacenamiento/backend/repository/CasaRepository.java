package com.almacenamiento.backend.repository;

import com.almacenamiento.backend.model.Casa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CasaRepository extends JpaRepository<Casa, Long> {
    Optional<Casa> findByCodigoInvitacion(String codigoInvitacion);
}