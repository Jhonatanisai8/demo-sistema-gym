package com.isai.gym.app.dtos;

import java.math.BigDecimal;

public class AdminDashboardStatsDTO {

    private BigDecimal totalVentas;
    private BigDecimal totalMembresiasPagadas;
    private long clientesActivos;
    private long entrenadoresTotales;

    public AdminDashboardStatsDTO() {
        this.totalVentas = BigDecimal.ZERO;
        this.totalMembresiasPagadas = BigDecimal.ZERO;
    }

    // Getters y Setters

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }

    public BigDecimal getTotalMembresiasPagadas() {
        return totalMembresiasPagadas;
    }

    public void setTotalMembresiasPagadas(BigDecimal totalMembresiasPagadas) {
        this.totalMembresiasPagadas = totalMembresiasPagadas;
    }

    public long getClientesActivos() {
        return clientesActivos;
    }

    public void setClientesActivos(long clientesActivos) {
        this.clientesActivos = clientesActivos;
    }

    public long getEntrenadoresTotales() {
        return entrenadoresTotales;
    }

    public void setEntrenadoresTotales(long entrenadoresTotales) {
        this.entrenadoresTotales = entrenadoresTotales;
    }

    // Método calculado para las ganancias totales
    public BigDecimal getGananciasTotales() {
        BigDecimal ventas = (this.totalVentas != null) ? this.totalVentas : BigDecimal.ZERO;
        BigDecimal membresias = (this.totalMembresiasPagadas != null) ? this.totalMembresiasPagadas : BigDecimal.ZERO;
        return ventas.add(membresias);
    }
}
