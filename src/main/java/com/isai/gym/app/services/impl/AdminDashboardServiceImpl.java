package com.isai.gym.app.services.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.isai.gym.app.enums.EstadoMembresia;
import com.isai.gym.app.enums.EstadoVenta;
import com.isai.gym.app.enums.TipoUsuario;
import com.isai.gym.app.dtos.AdminDashboardStatsDTO;
import com.isai.gym.app.repository.EntrenadorRepository;
import com.isai.gym.app.repository.MembresiaClienteRepository;
import com.isai.gym.app.repository.UsuarioRepository;
import com.isai.gym.app.repository.VentaRepository;
import com.isai.gym.app.services.AdminDashboardService;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final VentaRepository ventaRepository;
    private final MembresiaClienteRepository membresiaClienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EntrenadorRepository entrenadorRepository;

    public AdminDashboardServiceImpl(VentaRepository ventaRepository,
            MembresiaClienteRepository membresiaClienteRepository,
            UsuarioRepository usuarioRepository,
            EntrenadorRepository entrenadorRepository) {
        this.ventaRepository = ventaRepository;
        this.membresiaClienteRepository = membresiaClienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.entrenadorRepository = entrenadorRepository;
    }

    @Override
    public AdminDashboardStatsDTO getDashboardStats() {
        AdminDashboardStatsDTO statsDTO = new AdminDashboardStatsDTO();

        // 1. Calcular ganancias totales de ventas
        BigDecimal totalVentas = ventaRepository.sumTotalVentasByEstado(EstadoVenta.COMPLETADA);
        statsDTO.setTotalVentas(totalVentas != null ? totalVentas : BigDecimal.ZERO);

        // 2. Calcular total pagado de membresías
        List<EstadoMembresia> estadosMembresia = Arrays.asList(EstadoMembresia.ACTIVA, EstadoMembresia.EXPERADA);
        BigDecimal totalMembresias = membresiaClienteRepository.sumTotalMembresiasPagadasByEstadoIn(estadosMembresia);
        statsDTO.setTotalMembresiasPagadas(totalMembresias != null ? totalMembresias : BigDecimal.ZERO);

        // 3. Contar clientes activos
        long clientesActivos = usuarioRepository.countByRolAndActivo(TipoUsuario.CLIENTE, true);
        statsDTO.setClientesActivos(clientesActivos);

        // 4. Contar entrenadores totales (activos)
        long entrenadoresActivos = entrenadorRepository.countByActivo(true);
        statsDTO.setEntrenadoresTotales(entrenadoresActivos);

        return statsDTO;
    }
}
