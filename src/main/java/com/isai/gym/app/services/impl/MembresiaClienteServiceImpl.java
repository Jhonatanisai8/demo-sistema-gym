package com.isai.gym.app.services.impl;

import com.isai.gym.app.dtos.MembresiaClienteDTO;
import com.isai.gym.app.entities.Membresia;
import com.isai.gym.app.entities.MembresiaCliente;
import com.isai.gym.app.entities.Pago;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.enums.EstadoMembresia;
import com.isai.gym.app.repository.MembresiaClienteRepository;
import com.isai.gym.app.repository.MembresiaRepository;
import com.isai.gym.app.repository.PagoRepository;
import com.isai.gym.app.repository.UsuarioRepository;
import com.isai.gym.app.services.MembresiaClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MembresiaClienteServiceImpl implements MembresiaClienteService {

    private final MembresiaClienteRepository membresiaClienteRepository;

    private final MembresiaRepository membresiaRepository;

    private final UsuarioRepository usuarioRepository;

    private final PagoRepository pagoRepository;
    // Para buscar el usuario

    @Override
    @Transactional
    public MembresiaCliente guardar(MembresiaClienteDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + dto.getUsuarioId()));
        Membresia membresiaTipo = membresiaRepository.findById(dto.getMembresiaId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de membresía no encontrado con ID: " + dto.getMembresiaId()));

        MembresiaCliente membresiaCliente = new MembresiaCliente();
        membresiaCliente.setUsuario(usuario);
        membresiaCliente.setMembresia(membresiaTipo);
        membresiaCliente.setFechaInicio(dto.getFechaInicio());
        membresiaCliente.setFechaFin(dto.getFechaFin());
        membresiaCliente.setMontoPagado(dto.getMontoPagado());
        membresiaCliente.setMetodoPago(dto.getMetodoPago());
        membresiaCliente.setActiva(dto.getActiva() != null ? dto.getActiva() : true);
        membresiaCliente.setEstado(dto.getEstado() != null ? dto.getEstado() : EstadoMembresia.ACTIVA);
        return membresiaClienteRepository.save(membresiaCliente);
    }

    @Override
    @Transactional
    public Optional<MembresiaCliente> actualizar(Long id, MembresiaClienteDTO dto) {
        return membresiaClienteRepository.findById(id).map(membresiaExistente -> {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + dto.getUsuarioId()));
            Membresia membresiaTipo = membresiaRepository.findById(dto.getMembresiaId())
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de membresía no encontrado con ID: " + dto.getMembresiaId()));
            membresiaExistente.setUsuario(usuario);
            membresiaExistente.setMembresia(membresiaTipo);
            membresiaExistente.setFechaInicio(dto.getFechaInicio());
            membresiaExistente.setFechaFin(dto.getFechaFin());
            membresiaExistente.setMontoPagado(dto.getMontoPagado());
            membresiaExistente.setMetodoPago(dto.getMetodoPago());
            membresiaExistente.setActiva(dto.getActiva());
            membresiaExistente.setEstado(dto.getEstado());
            return membresiaClienteRepository.save(membresiaExistente);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MembresiaCliente> obtenerPorID(Long id) {
        return membresiaClienteRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MembresiaCliente> obtenerTodasMembresias(Pageable pageable) {
        return membresiaClienteRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MembresiaCliente> buscar(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return membresiaClienteRepository.searchByUsuarioNombreOrMembresiaNombre(keyword, pageable);
        }
        return membresiaClienteRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public boolean eliminarPorID(Long id) {
        if (membresiaClienteRepository.existsById(id)) {
            membresiaClienteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Optional<MembresiaCliente> toggleActiva(Long id, boolean activa) {
        return membresiaClienteRepository.findById(id).map(membresiaCliente -> {
            membresiaCliente.setActiva(activa);
            if (!activa) {
                membresiaCliente.setEstado(EstadoMembresia.SUSPENDIDA);
            } else {
                if (membresiaCliente.getFechaFin().isAfter(LocalDate.now())) {
                    membresiaCliente.setEstado(EstadoMembresia.ACTIVA);
                } else {
                    membresiaCliente.setEstado(EstadoMembresia.EXPERADA);
                }
            }
            return membresiaClienteRepository.save(membresiaCliente);
        });
    }

    @Override
    @Transactional
    public void actualizarEstadosMembresias() {
        LocalDate today = LocalDate.now();
        List<MembresiaCliente> membresiasAActualizar = membresiaClienteRepository.findAll(); // O una consulta más específica
        for (MembresiaCliente mc : membresiasAActualizar) {
            if (mc.getFechaFin().isBefore(today) && mc.getEstado() != EstadoMembresia.EXPERADA) {
                mc.setEstado(EstadoMembresia.EXPERADA); // O EXPIRADA
                mc.setActiva(false); // Una membresía expirada no debería estar activa
                membresiaClienteRepository.save(mc);
            } else if (mc.getFechaInicio().isAfter(today) && mc.getEstado() == EstadoMembresia.ACTIVA) {
            }
        }
    }

    @Override
    public List<MembresiaCliente> obtenerMembresiasPorUsuario(Long usuarioId) {
        return membresiaClienteRepository.findByUsuarioIdOrderByFechaFinDesc(usuarioId);
    }

    @Override
    public MembresiaCliente obtenerMembresiaActivaPorUsuario(Long usuarioId) {
        List<MembresiaCliente> activas = membresiaClienteRepository
                .findByUsuarioIdAndActivaTrueAndFechaFinAfter(usuarioId, LocalDate.now());
        return activas.isEmpty() ? null : activas.get(0); // Devuelve la primera activa encontrada, o null

    }

    @Override
    public MembresiaCliente adquirirMembresia(Usuario usuario, Membresia tipoMembresia, String metodoPago) {
        // creamos la instancia de MembresiaCliente
        MembresiaCliente nuevaMembresia = new MembresiaCliente();
        nuevaMembresia.setUsuario(usuario);
        nuevaMembresia.setMembresia(tipoMembresia);
        nuevaMembresia.setFechaCompra(LocalDateTime.now());
        nuevaMembresia.setFechaInicio(LocalDate.now());
        // calculamos la fecha de fin
        nuevaMembresia.setFechaFin(LocalDate.now().plusDays(tipoMembresia.getDuracionDias()));
        nuevaMembresia.setMontoPagado(tipoMembresia.getPrecio());
        nuevaMembresia.setMetodoPago(metodoPago);
        nuevaMembresia.setActiva(true);
        nuevaMembresia.setEstado(EstadoMembresia.ACTIVA);

        // guardamos la MembresiaCliente
        MembresiaCliente membresiaGuardada = membresiaClienteRepository.save(nuevaMembresia);

        //  registramos el pago
        Pago nuevoPago = new Pago();
        nuevoPago.setUsuario(usuario);
        nuevoPago.setConcepto("Compra de membresía: " + tipoMembresia.getNombre());
        nuevoPago.setMonto(tipoMembresia.getPrecio());
        nuevoPago.setMetodoPago(metodoPago);
        nuevoPago.setFechaPago(LocalDateTime.now());
        nuevoPago.setFechaRegistro(LocalDateTime.now());
        nuevoPago.setEstado("COMPLETADO");
        nuevoPago.setReferenciaId(membresiaGuardada.getId());
        pagoRepository.save(nuevoPago);

        return membresiaGuardada;
    }

    public Optional<MembresiaCliente> obtenerMembresiaActivaPorUsuarioId(Long usuarioId) {
        return membresiaClienteRepository.findTopByUsuarioIdAndEstadoOrderByFechaInicioDesc(usuarioId, EstadoMembresia.ACTIVA);
    }
}