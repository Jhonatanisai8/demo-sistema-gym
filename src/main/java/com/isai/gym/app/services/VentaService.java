package com.isai.gym.app.services;


import com.isai.gym.app.dtos.VentaDTO;
import com.isai.gym.app.enums.EstadoVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface VentaService {
    VentaDTO registrarVenta(VentaDTO ventaDTO);

    VentaDTO obtenerVentaPorId(Long id);

    void cancelarVenta(Long id);

    Page<VentaDTO> obtenerTodasLasVentas(Pageable pageable);

    Page<VentaDTO> buscarVentas(String keyword, Pageable pageable);

    Page<VentaDTO> obtenerVentasPorRangoDeFechas(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<VentaDTO> obtenerVentasPorCliente(Long usuarioId, Pageable pageable);

    Page<VentaDTO> obtenerVentasPorEstado(EstadoVenta estado, Pageable pageable);
}