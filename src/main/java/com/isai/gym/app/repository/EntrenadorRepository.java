package com.isai.gym.app.repository;

import com.isai.gym.app.entities.Entrenador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntrenadorRepository
        extends JpaRepository<Entrenador, Long> {

    // metodo para buscar por nombre o especialidad
    Page<Entrenador> findByNombreContainingIgnoreCaseOrEspecialidadContainingIgnoreCase(String nombre, String especialidad, Pageable pageable);

    // método para verificar unicidad de nombre (opcional, pero útil para evitar duplicados)
    Optional<Entrenador> findByNombre(String nombre);

    // método para verificar unicidad de email (muy recomendable)
    Optional<Entrenador> findByEmail(String email);

    long countByActivo(boolean activo);
}
