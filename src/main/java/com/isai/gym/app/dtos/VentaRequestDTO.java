package com.isai.gym.app.dtos;

import com.isai.gym.app.enums.MetodoPago;
import lombok.Data;

@Data
public class VentaRequestDTO {
    private MetodoPago metodoPago;
}
