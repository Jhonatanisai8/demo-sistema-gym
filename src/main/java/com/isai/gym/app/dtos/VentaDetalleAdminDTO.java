package com.isai.gym.app.dtos;

import java.math.BigDecimal;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentaDetalleAdminDTO {
    private VentaHistorialDTO ventaInfo;
    private BigDecimal descuento;
    private List<ItemVentaHistorialDTO> items;
}
