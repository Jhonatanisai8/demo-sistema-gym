package com.isai.gym.app.repository;

import com.isai.gym.app.entities.Venta;
import com.isai.gym.app.enums.EstadoVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("SELECT v FROM Venta v WHERE " +
            "(v.vendedor IS NOT NULL AND LOWER(v.vendedor.nombreCompleto) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
            "(v.usuario IS NOT NULL AND LOWER(v.usuario.nombreCompleto) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
            "LOWER(v.metodoPago) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(v.estado) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Venta> buscarVentasPorKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Venta> findByFechaVentaBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    Page<Venta> findByUsuarioId(Long usuarioId, Pageable pageable);

    Page<Venta> findByEstado(EstadoVenta estado, Pageable pageable);
}