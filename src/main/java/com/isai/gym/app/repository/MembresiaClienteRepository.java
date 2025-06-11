package com.isai.gym.app.repository;

import com.isai.gym.app.entities.MembresiaCliente;
import com.isai.gym.app.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembresiaClienteRepository
        extends JpaRepository<MembresiaCliente, Long> {

    //membresias activas por usuario
    List<MembresiaCliente> findByUsuarioAndActivaTrue(Usuario usuario);

    //membresia actual de un usuario
    Optional<MembresiaCliente> findByUsuarioAndActivaTrueAndFechaFinGreaterThanEqual(Usuario usuario, LocalDate fechaActual);

    // En IMembresiaClienteRepository.java
    @Query("SELECT mc FROM MembresiaCliente mc JOIN mc.usuario u JOIN mc.membresia m " +
            "WHERE LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.nombre) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<MembresiaCliente> searchByUsuarioNombreOrMembresiaNombre(@Param("keyword") String keyword, Pageable pageable);

    List<MembresiaCliente> findByUsuarioIdOrderByFechaFinDesc(Long id);

    List<MembresiaCliente> findByUsuarioIdAndActivaTrueAndFechaFinAfter(Long id, LocalDate fechaActual);
}
