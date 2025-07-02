package com.isai.gym.app.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.isai.gym.app.dtos.CarritoDTO;
import com.isai.gym.app.dtos.VentaDTO;
import com.isai.gym.app.dtos.VentaDetalleAdminDTO;
import com.isai.gym.app.dtos.VentaHistorialDTO;
import com.isai.gym.app.dtos.VentaHistorialFilterDTO;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.entities.Venta;
import com.isai.gym.app.enums.EstadoVenta;
import com.isai.gym.app.enums.MetodoPago;

public interface VentaService {
    Venta crearVenta(CarritoDTO carrito, Usuario usuario, MetodoPago metodoPago);

    VentaDTO registrarVenta(VentaDTO ventaDTO);

    VentaDTO obtenerVentaPorId(Long id);

    void cancelarVenta(Long id);

    Page<VentaDTO> obtenerTodasLasVentas(Pageable pageable);

    Page<VentaDTO> buscarVentas(String keyword, Pageable pageable);

    Page<VentaDTO> obtenerVentasPorRangoDeFechas(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<VentaDTO> obtenerVentasPorCliente(Long usuarioId, Pageable pageable);

    Page<VentaDTO> obtenerVentasPorEstado(EstadoVenta estado, Pageable pageable);

    Page<VentaHistorialDTO> obtenerHistorialVentas(VentaHistorialFilterDTO filtros, Pageable pageable);

    Optional<VentaDetalleAdminDTO> obtenerDetalleVenta(Long ventaId);

}