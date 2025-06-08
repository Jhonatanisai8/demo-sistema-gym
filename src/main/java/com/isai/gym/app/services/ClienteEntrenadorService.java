package com.isai.gym.app.services;

import com.isai.gym.app.dtos.ClienteEntrenadorDTO;
import com.isai.gym.app.entities.ClienteEntrenador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;

public interface ClienteEntrenadorService {

    /**
     * Guarda una nueva asignación de cliente a entrenador.
     *
     * @param dto DTO con los datos de la asignación.
     * @return La entidad ClienteEntrenador guardada.
     * @throws IllegalArgumentException Si el usuario o entrenador no son encontrados, o hay conflictos de fechas.
     */
    ClienteEntrenador guardar(ClienteEntrenadorDTO dto);

    /**
     * Actualiza una asignación de cliente a entrenador existente.
     *
     * @param id  ID de la asignación a actualizar.
     * @param dto DTO con los datos actualizados.
     * @return La entidad ClienteEntrenador actualizada o Optional.empty() si no se encontró.
     * @throws IllegalArgumentException Si el usuario o entrenador no son encontrados, o hay conflictos de fechas.
     */
    Optional<ClienteEntrenador> actualizar(Long id, ClienteEntrenadorDTO dto);

    /**
     * Busca una asignación por su ID.
     *
     * @param id ID de la asignación.
     * @return Un Optional que contiene la ClienteEntrenador si se encuentra, o vacío si no.
     */
    Optional<ClienteEntrenador> obtenerPorID(Long id);

    /**
     * Obtiene todas las asignaciones con paginación y ordenamiento.
     *
     * @param pageable Objeto Pageable para configurar la paginación.
     * @return Una página de asignaciones.
     */
    Page<ClienteEntrenador> obtenerClientesEntrenadores(Pageable pageable);

    /**
     * Busca asignaciones por el nombre del usuario o el nombre del entrenador.
     *
     * @param keyword  Palabra clave para buscar.
     * @param pageable Objeto Pageable para configurar la paginación.
     * @return Una página de asignaciones que coinciden con la palabra clave.
     */
    Page<ClienteEntrenador> buscar(String keyword, Pageable pageable);

    /**
     * Elimina una asignación por su ID.
     *
     * @param id ID de la asignación a eliminar.
     * @return true si se eliminó exitosamente, false si no se encontró.
     */
    boolean eliminarPorID(Long id);

    /**
     * Obtiene todas las asignaciones de un usuario específico.
     *
     * @param usuarioId ID del usuario.
     * @return Lista de asignaciones.
     */
    List<ClienteEntrenador> obtenerUsuarioID(Long usuarioId);

    /**
     * Obtiene todas las asignaciones de un entrenador específico.
     *
     * @param entrenadorId ID del entrenador.
     * @return Lista de asignaciones.
     */
    List<ClienteEntrenador> obtenerClienteEntrenadorID(Long entrenadorId);
}