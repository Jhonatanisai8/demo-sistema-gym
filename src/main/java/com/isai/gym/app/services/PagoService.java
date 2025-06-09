package com.isai.gym.app.services;

import com.isai.gym.app.dtos.PagoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PagoService {

    PagoDTO registrarPago(PagoDTO pago);

    Page<PagoDTO> obtenerHistorialPagos(Pageable pageable, String keyword);

    Optional<PagoDTO> obtenerPagoPorId(Long id);
}
