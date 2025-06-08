package com.isai.gym.app.services.impl;

import com.isai.gym.app.dtos.ClienteEntrenadorDTO;
import com.isai.gym.app.entities.ClienteEntrenador;
import com.isai.gym.app.entities.Entrenador;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.repository.ClienteEntrenadorRepository;
import com.isai.gym.app.repository.EntrenadorRepository;
import com.isai.gym.app.repository.UsuarioRepository;
import com.isai.gym.app.services.ClienteEntrenadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClienteEntrenadorServiceImpl
        implements ClienteEntrenadorService {

    private final ClienteEntrenadorRepository clienteEntrenadorRepository;

    private final UsuarioRepository usuarioRepository;

    private final EntrenadorRepository entrenadorRepository;

    @Override
    public ClienteEntrenador guardar(ClienteEntrenadorDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + dto.getUsuarioId()));
        Entrenador entrenador = entrenadorRepository.findById(dto.getEntrenadorId())
                .orElseThrow(() -> new IllegalArgumentException("Entrenador no encontrado con ID: " + dto.getEntrenadorId()));

        List<ClienteEntrenador> conflictos = clienteEntrenadorRepository.findConflictingAssignments(
                dto.getUsuarioId(), dto.getEntrenadorId(), dto.getFechaInicio(), dto.getFechaFin());
        if (!conflictos.isEmpty()) {
            throw new IllegalArgumentException("Ya existe una asignación para este cliente y entrenador en el rango de fechas especificado.");
        }

        ClienteEntrenador clienteEntrenador = new ClienteEntrenador();
        clienteEntrenador.setUsuario(usuario);
        clienteEntrenador.setEntrenador(entrenador);
        clienteEntrenador.setFechaInicio(dto.getFechaInicio());
        clienteEntrenador.setFechaFin(dto.getFechaFin());

        return clienteEntrenadorRepository.save(clienteEntrenador);
    }

    @Override
    public Optional<ClienteEntrenador> actualizar(Long id, ClienteEntrenadorDTO dto) {
        return clienteEntrenadorRepository.findById(id).map(clienteEntrenadorExistente -> {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + dto.getUsuarioId()));
            Entrenador entrenador = entrenadorRepository.findById(dto.getEntrenadorId())
                    .orElseThrow(() -> new IllegalArgumentException("Entrenador no encontrado con ID: " + dto.getEntrenadorId()));
            //  Validar conflictos, excluyendo la propia asignación que se está editando
            List<ClienteEntrenador> conflictos = clienteEntrenadorRepository.findConflictingAssignments(
                    dto.getUsuarioId(), dto.getEntrenadorId(), dto.getFechaInicio(), dto.getFechaFin());
            for (ClienteEntrenador conflicto : conflictos) {
                if (!conflicto.getId().equals(id)) {
                    throw new IllegalArgumentException("Ya existe una asignación para este cliente y entrenador en el rango de fechas especificado.");
                }
            }
            clienteEntrenadorExistente.setUsuario(usuario);
            clienteEntrenadorExistente.setEntrenador(entrenador);
            clienteEntrenadorExistente.setFechaInicio(dto.getFechaInicio());
            clienteEntrenadorExistente.setFechaFin(dto.getFechaFin());
            return clienteEntrenadorRepository.save(clienteEntrenadorExistente);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteEntrenador> obtenerPorID(Long id) {
        return clienteEntrenadorRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteEntrenador> obtenerClientesEntrenadores(Pageable pageable) {
        return clienteEntrenadorRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteEntrenador> buscar(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return clienteEntrenadorRepository.findAll(pageable);
        }
        return clienteEntrenadorRepository.searchByUsuarioOrEntrenadorName(keyword, pageable);
    }

    @Override
    public boolean eliminarPorID(Long id) {
        if (clienteEntrenadorRepository.existsById(id)) {
            clienteEntrenadorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteEntrenador> obtenerUsuarioID(Long usuarioId) {
        return clienteEntrenadorRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<ClienteEntrenador> obtenerClienteEntrenadorID(Long entrenadorId) {
        return clienteEntrenadorRepository.findByEntrenadorId(entrenadorId);
    }
}
