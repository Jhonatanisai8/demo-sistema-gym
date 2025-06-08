package com.isai.gym.app.repository;

import com.isai.gym.app.entities.ClienteEntrenador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

import java.time.LocalDate;
import java.util.*;

@Repository
public interface ClienteEntradorRespository
        extends JpaRepository<ClienteEntrenador, Long> {
    // Buscar asignaciones por el ID del usuario
    List<ClienteEntrenador> findByUsuarioId(Long usuarioId);

    // Buscar asignaciones por el ID del entrenador
    List<ClienteEntrenador> findByEntrenadorId(Long entrenadorId);

    // Buscar asignaciones activas (donde fechaFin es nula o en el futuro)
    List<ClienteEntrenador> findByFechaFinIsNullOrFechaFinAfter(LocalDate fechaActual);

    // Búsqueda por nombre de usuario o nombre de entrenador
    @Query("SELECT ce FROM ClienteEntrenador ce JOIN ce.usuario u JOIN ce.entrenador e " +
            "WHERE LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.nombre) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ClienteEntrenador> searchByUsuarioOrEntrenadorName(@Param("keyword") String keyword, Pageable pageable);

    //  evitar asignaciones duplicadas de un cliente a un entrenador en un rango de fechas.
    // opcional, pero importante si quieres evitar superposiciones.
    @Query("SELECT ce FROM ClienteEntrenador ce " +
            "WHERE ce.usuario.id = :usuarioId AND ce.entrenador.id = :entrenadorId " +
            "AND (:fechaInicio BETWEEN ce.fechaInicio AND COALESCE(ce.fechaFin, '2099-12-31')) " + // COALESCE maneja fechaFin nula
            "OR (:fechaFin BETWEEN ce.fechaInicio AND COALESCE(ce.fechaFin, '2099-12-31'))")
    List<ClienteEntrenador> findConflictingAssignments(
            @Param("usuarioId") Long usuarioId,
            @Param("entrenadorId") Long entrenadorId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}
