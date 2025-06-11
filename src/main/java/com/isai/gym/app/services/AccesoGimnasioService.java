package com.isai.gym.app.services;

import com.isai.gym.app.entities.AccesoGimnasio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccesoGimnasioService {

    Page<AccesoGimnasio> obtenerAccesosPorUsuario(Long usuarioId, Pageable paginacion);

    List<AccesoGimnasio> obtenerUltimosAccesosPorUsuario(Long usuarioId, int limite);

}
