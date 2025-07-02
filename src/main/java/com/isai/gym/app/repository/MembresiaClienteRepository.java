package com.isai.gym.app.repository;

import com.isai.gym.app.entities.MembresiaCliente;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.enums.EstadoMembresia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembresiaClienteRepository
        extends JpaRepository<MembresiaCliente, Long> {

    // Membresías activas por usuario
    List<MembresiaCliente> findByUsuarioAndActivaTrue(Usuario usuario);

    // Membresía actual de un usuario que aún no ha terminado
    Optional<MembresiaCliente> findByUsuarioAndActivaTrueAndFechaFinGreaterThanEqual(Usuario usuario, LocalDate fechaActual);

    // Búsqueda de membresías por nombre completo de usuario o nombre de membresía (paginada)
    @Query("SELECT mc FROM MembresiaCliente mc JOIN mc.usuario u JOIN mc.membresia m " +
            "WHERE LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.nombre) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<MembresiaCliente> searchByUsuarioNombreOrMembresiaNombre(@Param("keyword") String keyword, Pageable pageable);

    // Lista de membresías de un usuario ordenadas por fecha de fin descendente
    List<MembresiaCliente> findByUsuarioIdOrderByFechaFinDesc(Long id);

    // Lista de membresías activas de un usuario cuya fecha de fin es posterior a la fecha actual
    List<MembresiaCliente> findByUsuarioIdAndActivaTrueAndFechaFinAfter(Long id, LocalDate fechaActual);

    // Nuevo método para obtener la membresía activa más reciente para un usuario.
    Optional<MembresiaCliente> findTopByUsuarioIdAndEstadoOrderByFechaInicioDesc(Long usuarioId, EstadoMembresia estado);

    // Método para sumar el total de membresías pagadas por estado
    @Query("SELECT SUM(mc.montoPagado) FROM MembresiaCliente mc WHERE mc.estado IN :estados")
    BigDecimal sumTotalMembresiasPagadasByEstadoIn(@Param("estados") List<EstadoMembresia> estados);
}
