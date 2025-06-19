package com.almacenamiento.backend.repository;

import com.almacenamiento.backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCasaId(Long casaId);
}