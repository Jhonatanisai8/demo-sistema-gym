package com.isai.gym.app.services;

import com.isai.gym.app.dtos.EntrenadorDTO;
import com.isai.gym.app.entities.Entrenador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;

public interface EntrenadorService {

    /**
     * Guarda un nuevo entrenador, incluyendo la gestión de la imagen.
     *
     * @param entrenadorDTO DTO con los datos del entrenador y la imagen.
     * @return La entidad Entrenador guardada.
     * @throws IllegalArgumentException Si el nombre o email ya existen.
     */
    Entrenador guardar(EntrenadorDTO entrenadorDTO);

    /**
     * Actualiza un entrenador existente, incluyendo la gestión de la imagen.
     *
     * @param id            ID del entrenador a actualizar.
     * @param entrenadorDTO DTO con los datos actualizados y la nueva imagen (opcional).
     * @return La entidad Entrenador actualizada o Optional.empty() si no se encontró.
     * @throws IllegalArgumentException Si el nombre o email ya existen (para otro entrenador).
     */
    Optional<Entrenador> actualizar(Long id, EntrenadorDTO entrenadorDTO);

    /**
     * Busca un entrenador por su ID.
     *
     * @param id ID del entrenador.
     * @return Un Optional que contiene el Entrenador si se encuentra, o vacío si no.
     */
    Optional<Entrenador> findById(Long id);

    /**
     * Obtiene todos los entrenadores con paginación y ordenamiento.
     *
     * @param pageable Objeto Pageable para configurar la paginación.
     * @return Una página de entrenadores.
     */
    Page<Entrenador> obtenerEntrenadores(Pageable pageable);

    /**
     * Obtiene una lista de todos los entrenadores (útil para comboboxes).
     *
     * @return Lista de entrenadores.
     */
    List<Entrenador> obtenerEntrenadores();

    /**
     * Busca entrenadores por nombre o especialidad.
     *
     * @param keyword  Palabra clave para buscar.
     * @param pageable Objeto Pageable para configurar la paginación.
     * @return Una página de entrenadores que coinciden con la palabra clave.
     */
    Page<Entrenador> buscar(String keyword, Pageable pageable);

    /**
     * Elimina un entrenador por su ID.
     *
     * @param id ID del entrenador a eliminar.
     * @return true si se eliminó exitosamente, false si no se encontró o hubo un error (ej. asignaciones).
     */
    boolean eliminarPorID(Long id);

    /**
     * Cambia el estado 'activo' de un entrenador.
     *
     * @param id     ID del entrenador.
     * @param activo Nuevo estado (true para activo, false para inactivo).
     * @return El entrenador actualizado o Optional.empty() si no se encontró.
     */
    Optional<Entrenador> toggleActivo(Long id, boolean activo);

    /**
     * Verifica si un nombre de entrenador ya existe (excluyendo el propio ID en caso de edición).
     *
     * @param nombre Nombre a verificar.
     * @param id     ID del entrenador actual (nulo para nuevos entrenadores).
     * @return true si el nombre ya existe, false en caso contrario.
     */
    boolean existeNombre(String nombre, Long id);

    /**
     * Verifica si un email de entrenador ya existe (excluyendo el propio ID en caso de edición).
     *
     * @param email Email a verificar.
     * @param id    ID del entrenador actual (nulo para nuevos entrenadores).
     * @return true si el email ya existe, false en caso contrario.
     */
    boolean existeEmail(String email, Long id);
}