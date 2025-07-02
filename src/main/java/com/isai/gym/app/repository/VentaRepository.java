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
import java.util.Optional;

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

    @Query("SELECT SUM(v.montoTotal) FROM Venta v WHERE v.estado = :estado")
    java.math.BigDecimal sumTotalVentasByEstado(@Param("estado") EstadoVenta estado);

    @Query("SELECT v FROM Venta v " +
            "LEFT JOIN v.usuario c " +
            "LEFT JOIN v.vendedor ve " +
            "WHERE (:fechaInicio IS NULL OR v.fechaVenta >= :fechaInicio) " +
            "AND (:fechaFin IS NULL OR v.fechaVenta <= :fechaFin) " +
            "AND (:estado IS NULL OR v.estado = :estado) " +
            "AND (:keywordCliente IS NULL OR LOWER(c.nombreCompleto) LIKE LOWER(CONCAT('%', :keywordCliente, '%'))) " +
            "AND (:keywordVendedor IS NULL OR LOWER(ve.nombreCompleto) LIKE LOWER(CONCAT('%', :keywordVendedor, '%'))) "
            +
            "ORDER BY v.fechaVenta DESC")


    Page<Venta> findFilteredVentas(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("estado") EstadoVenta estado,
            @Param("keywordCliente") String keywordCliente,
            @Param("keywordVendedor") String keywordVendedor,
            Pageable pageable);

    @Query("SELECT v FROM Venta v " +
            "LEFT JOIN FETCH v.items iv " +
            "LEFT JOIN FETCH iv.producto p " +
            "LEFT JOIN FETCH v.usuario c " +
            "LEFT JOIN FETCH v.vendedor ve " +
            "WHERE v.id = :id")
    Optional<Venta> findByIdWithDetails(@Param("id") Long id);
}