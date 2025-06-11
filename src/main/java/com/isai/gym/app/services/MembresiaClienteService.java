package com.isai.gym.app.services;

import com.isai.gym.app.dtos.MembresiaClienteDTO;
import com.isai.gym.app.entities.Membresia;
import com.isai.gym.app.entities.MembresiaCliente;
import com.isai.gym.app.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MembresiaClienteService {

    MembresiaCliente guardar(MembresiaClienteDTO dto);

    Optional<MembresiaCliente> actualizar(Long id, MembresiaClienteDTO dto);

    Optional<MembresiaCliente> obtenerPorID(Long id);

    Page<MembresiaCliente> obtenerTodasMembresias(Pageable pageable);

    Page<MembresiaCliente> buscar(String keyword, Pageable pageable);

    boolean eliminarPorID(Long id);

    Optional<MembresiaCliente> toggleActiva(Long id, boolean activa);

    void actualizarEstadosMembresias();

    List<MembresiaCliente> obtenerMembresiasPorUsuario(Long usuarioId);

    MembresiaCliente obtenerMembresiaActivaPorUsuario(Long usuarioId);

    MembresiaCliente adquirirMembresia(Usuario usuario, Membresia tipoMembresia, String metodoPago);
}