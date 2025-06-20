package com.almacenamiento.backend.repository;

import com.almacenamiento.backend.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecetaRepository extends JpaRepository<Receta, Long> {
    List<Receta> findByCasaId(Long casaId);
}
