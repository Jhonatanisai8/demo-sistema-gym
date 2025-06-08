package com.isai.gym.app.repository;

import com.isai.gym.app.entities.Membresia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembresiaRepository
        extends JpaRepository<Membresia, Long> {
    Optional<Membresia> findByNombre(String nombre);

    Page<Membresia> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String nombre, String descripcion, Pageable pageable);

    Page<Membresia> findByActivaTrue(Pageable pageable);

}
