package com.isai.gym.app.repository;

import com.isai.gym.app.entities.AccesoGimnasio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccesoGimnacioRepository
        extends JpaRepository<AccesoGimnasio, Long> {

    Page<AccesoGimnasio> findByUsuarioId(Long usuarioId, Pageable paginacion);

    List<AccesoGimnasio> findTop5ByUsuarioIdOrderByFechaHoraEntradaDesc(Long usuarioId);

    Page<AccesoGimnasio> findBy(Pageable paginacion); // Método genérico para todos los accesos sin filtro inicial

    Page<AccesoGimnasio> findByUsuarioIdAndFechaHoraEntradaBetween(Long usuarioId, LocalDateTime fechaDesde, LocalDateTime fechaHasta, Pageable paginacion);

    Page<AccesoGimnasio> findByFechaHoraEntradaBetween(LocalDateTime fechaDesde, LocalDateTime fechaHasta, Pageable paginacion);

    Optional<AccesoGimnasio> findTopByUsuarioIdAndActivoTrueOrderByFechaHoraEntradaDesc(Long usuarioId);

    long countByUsuarioIdAndFechaHoraEntradaBetween(Long usuarioId, LocalDateTime inicioDia, LocalDateTime finDia);

    List<AccesoGimnasio> findTop10ByOrderByFechaHoraEntradaDesc(); // Los 10 últimos accesos en general

}
