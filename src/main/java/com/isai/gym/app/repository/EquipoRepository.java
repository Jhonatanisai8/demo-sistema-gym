package com.isai.gym.app.repository;

import com.isai.gym.app.entities.Equipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipoRepository
        extends JpaRepository<Equipo, Long> {
    //METODOS DE BUSQUEDA
    Optional<Equipo> findByNombre(String nombre);

    Page<Equipo> findEquipoByNombreContaining(String nombre, Pageable pageable);
}
