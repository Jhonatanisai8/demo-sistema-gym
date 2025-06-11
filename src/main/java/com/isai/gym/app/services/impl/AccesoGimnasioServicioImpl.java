package com.isai.gym.app.services.impl;

import com.isai.gym.app.entities.AccesoGimnasio;
import com.isai.gym.app.entities.MembresiaCliente;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.repository.AccesoGimnacioRepository;
import com.isai.gym.app.services.AccesoGimnasioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccesoGimnasioServicioImpl
        implements AccesoGimnasioService {

    private final AccesoGimnacioRepository accesoGimnacioRepository;

    private final MembresiaClienteServiceImpl membresiaClienteService;

    private final AccesoGimnacioRepository accesoGimnasioRepositorio;

    private final UsuarioServiceImpl usuarioService;

    @Override
    public Page<AccesoGimnasio> obtenerAccesosPorUsuario(Long usuarioId, Pageable paginacion) {
        return accesoGimnacioRepository.findByUsuarioId(usuarioId, paginacion);
    }

    @Override
    public List<AccesoGimnasio> obtenerUltimosAccesosPorUsuario(Long usuarioId, int limite) {
        return accesoGimnacioRepository.findTop5ByUsuarioIdOrderByFechaHoraEntradaDesc(usuarioId);
    }

    public Page<AccesoGimnasio> obtenerTodosLosAccesos(
            String filtroUsuario,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Pageable paginacion) {

        Long usuarioId = null;
        if (filtroUsuario != null && !filtroUsuario.trim().isEmpty()) {
            Optional<Usuario> usuarioOpt = usuarioService.buscarUsuarioPorIdentificador(filtroUsuario.trim());
            if (usuarioOpt.isPresent()) {
                usuarioId = usuarioOpt.get().getId();
            } else {
                return Page.empty(paginacion);
            }
        }

        LocalDateTime fechaHoraDesde = (fechaDesde != null) ? fechaDesde.atStartOfDay() : null;
        LocalDateTime fechaHoraHasta = (fechaHasta != null) ? fechaHasta.atTime(23, 59, 59) : null;

        if (usuarioId != null && fechaHoraDesde != null && fechaHoraHasta != null) {
            return accesoGimnasioRepositorio.findByUsuarioIdAndFechaHoraEntradaBetween(usuarioId, fechaHoraDesde, fechaHoraHasta, paginacion);
        } else if (usuarioId != null) {
            return accesoGimnasioRepositorio.findByUsuarioId(usuarioId, paginacion);
        } else if (fechaHoraDesde != null && fechaHoraHasta != null) {
            return accesoGimnasioRepositorio.findByFechaHoraEntradaBetween(fechaHoraDesde, fechaHoraHasta, paginacion);
        } else {
            // Sin filtros, obtener todos los accesos
            return accesoGimnasioRepositorio.findAll(paginacion);
        }
    }

    @Transactional
    public AccesoGimnasio registrarEntrada(Usuario usuario) {
        // Validaciones:
        MembresiaCliente membresiaActiva = membresiaClienteService.obtenerMembresiaActivaPorUsuario(usuario.getId());

        if (membresiaActiva == null || !membresiaActiva.getActiva() || membresiaActiva.getFechaFin().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Acceso denegado: El cliente " + usuario.getNombreCompleto() + " no tiene una membresía activa o válida.");
        }

        // Opcional: Verificar si ya tiene una sesión de acceso activa (entrada sin salida)
        Optional<AccesoGimnasio> accesoActivo = accesoGimnasioRepositorio.findTopByUsuarioIdAndActivoTrueOrderByFechaHoraEntradaDesc(usuario.getId());
        if (accesoActivo.isPresent()) {
            throw new IllegalArgumentException("Acceso denegado: El cliente " + usuario.getNombreCompleto() + " ya tiene un acceso activo sin salida registrada.");
        }


        AccesoGimnasio acceso = new AccesoGimnasio();
        acceso.setUsuario(usuario);
        acceso.setFechaHoraEntrada(LocalDateTime.now());
        acceso.setActivo(true);
        return accesoGimnasioRepositorio.save(acceso);
    }

    @Transactional
    public AccesoGimnasio registrarSalida(Long idAccesoGimnasio) {
        AccesoGimnasio acceso = accesoGimnasioRepositorio.findById(idAccesoGimnasio)
                .orElseThrow(() -> new IllegalArgumentException("Registro de acceso no encontrado con ID: " + idAccesoGimnasio));

        if (!acceso.getActivo()) {
            throw new IllegalArgumentException("El acceso ID " + idAccesoGimnasio + " ya ha sido finalizado.");
        }

        acceso.setFechaHoraSalida(LocalDateTime.now());
        acceso.setActivo(false);
        return accesoGimnasioRepositorio.save(acceso);
    }

    public Optional<AccesoGimnasio> obtenerAccesoActivoPorUsuario(Long id) {
        return accesoGimnasioRepositorio.findTopByUsuarioIdAndActivoTrueOrderByFechaHoraEntradaDesc(id);
    }

    public List<AccesoGimnasio> obtenerUltimosAccesosPorUsuarioId(Long usuarioId, int limite) {
        // obtenemos todos los accesos ordenados y luego limitar en memoria.
        return accesoGimnasioRepositorio.findByUsuarioIdOrderByFechaHoraEntradaDesc(usuarioId)
                .stream()
                .limit(limite)
                .collect(Collectors.toList());
    }

}
