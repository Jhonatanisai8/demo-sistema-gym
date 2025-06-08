package com.isai.gym.app.repository;

import com.isai.gym.app.entities.MembresiaCliente;
import com.isai.gym.app.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembresiaClienteRepository
        extends JpaRepository<MembresiaCliente, Long> {

    //membresias activas por usuario
    List<MembresiaCliente> findByUsuarioAndActivaTrue(Usuario usuario);

    //membresia actual de un usuario
    Optional<MembresiaCliente> findByUsuarioAndActivaTrueAndFechaFinGreaterThanEqual(Usuario usuario, LocalDate fechaActual);


}
