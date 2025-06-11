package com.isai.gym.app.repository;

import com.isai.gym.app.entities.AccesoGimnasio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccesoGimnacioRepository
        extends JpaRepository<AccesoGimnasio, Long> {

    Page<AccesoGimnasio> findByUsuarioId(Long usuarioId, Pageable paginacion);

    List<AccesoGimnasio> findTop5ByUsuarioIdOrderByFechaHoraEntradaDesc(Long usuarioId);

}
