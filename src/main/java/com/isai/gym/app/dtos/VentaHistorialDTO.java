package com.isai.gym.app.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.isai.gym.app.enums.EstadoVenta;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentaHistorialDTO {
    private Long id;
    private LocalDateTime fechaVenta;
    private BigDecimal montoTotal;
    private EstadoVenta estado;
    private String metodoPago;
    private String nombreCliente;
    private String nombreVendedor;

}
