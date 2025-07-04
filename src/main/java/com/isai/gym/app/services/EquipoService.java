package com.isai.gym.app.services;

import com.isai.gym.app.dtos.EquipoDTO;
import com.isai.gym.app.entities.Equipo;
import com.isai.gym.app.enums.EstadoEquipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EquipoService {

    Equipo guardarEquipo(EquipoDTO equipoDTO);

    Optional<Equipo> obtenerPorId(Long id);

    Optional<Equipo> actualizarEquipo(Long id, EquipoDTO equipo);

    Page<Equipo> buscarEquiposPorNombre(String nombre, Pageable pageable);

    Page<Equipo> obtenerEquipos(Pageable pageable);

    boolean eliminarPorId(Long id);

    boolean existeEquipoNombre(String nombre, Long id);

    Optional<Equipo> cambiarEstadoEquipo(Long id, EstadoEquipo estadoEquipo);

    List<Equipo> obtenerEquiposDisponibles();

    String obtenerDescripcionInventarioDisponible();
}
