package com.isai.gym.app.services;

import com.isai.gym.app.dtos.MembresiaDTO;
import com.isai.gym.app.entities.Membresia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MembresiaService {

    Membresia guardarMembresia(MembresiaDTO membresiaDTO);

    Optional<Membresia> actualizar(Long id, MembresiaDTO membresiaDTO);

    Optional<Membresia> obtenerMembresiaId(Long id);

    Page<Membresia> obtenerMembresias(Pageable pageable);

    Page<Membresia> buscar(String keyword, Pageable pageable);

    boolean eliminar(Long id);

    boolean existeNombre(String nombre, Long id);

    Optional<Membresia> cambiarEstado(Long id, boolean activa);
}
