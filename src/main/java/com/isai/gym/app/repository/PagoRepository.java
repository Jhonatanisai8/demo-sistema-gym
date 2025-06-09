package com.isai.gym.app.repository;

import com.isai.gym.app.entities.Pago;
import com.isai.gym.app.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    Page<Pago> findByUsuario(Usuario usuario, Pageable pageable);


    Page<Pago> findByFechaPagoBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


    Page<Pago> findByMetodoPagoContainingIgnoreCase(String metodoPago, Pageable pageable);


    @Query("SELECT p FROM Pago p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(p.concepto) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.metodoPago) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.estado) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.observaciones) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.usuario.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.usuario.apellido) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(p.referenciaId AS string) LIKE CONCAT('%', :keyword, '%'))")
    Page<Pago> buscarPagosPorKeyword(@Param("keyword") String keyword, Pageable pageable);


}