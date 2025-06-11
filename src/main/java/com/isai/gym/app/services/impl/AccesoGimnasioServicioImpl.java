package com.isai.gym.app.services.impl;

import com.isai.gym.app.entities.AccesoGimnasio;
import com.isai.gym.app.repository.AccesoGimnacioRepository;
import com.isai.gym.app.services.AccesoGimnasioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccesoGimnasioServicioImpl
        implements AccesoGimnasioService {

    private final AccesoGimnacioRepository accesoGimnacioRepository;

    @Override
    public Page<AccesoGimnasio> obtenerAccesosPorUsuario(Long usuarioId, Pageable paginacion) {
        return accesoGimnacioRepository.findByUsuarioId(usuarioId, paginacion);
    }

    @Override
    public List<AccesoGimnasio> obtenerUltimosAccesosPorUsuario(Long usuarioId, int limite) {
        return accesoGimnacioRepository.findTop5ByUsuarioIdOrderByFechaHoraEntradaDesc(usuarioId);
    }

    // Si necesitaras un método para registrar una entrada/salida (aunque no es el objetivo de este módulo):
    /*
    @Transactional
    public AccesoGimnasio registrarEntrada(Usuario usuario) {
        AccesoGimnasio acceso = new AccesoGimnasio();
        acceso.setUsuario(usuario);
        acceso.setFechaHoraEntrada(LocalDateTime.now());
        acceso.setActivo(true); // Indica que el usuario está actualmente dentro
        return accesoGimnasioRepositorio.save(acceso);
    }

    @Transactional
    public AccesoGimnasio registrarSalida(Long accesoId) {
        AccesoGimnasio accesoExistente = accesoGimnasioRepositorio.findById(accesoId)
                .orElseThrow(() -> new IllegalArgumentException("Registro de acceso no encontrado."));
        accesoExistente.setFechaHoraSalida(LocalDateTime.now());
        accesoExistente.setActivo(false); // Indica que el usuario ya salió
        return accesoGimnasioRepositorio.save(accesoExistente);
    }
    */
}
