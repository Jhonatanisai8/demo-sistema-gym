package com.isai.gym.app.dtos;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.isai.gym.app.enums.EstadoVenta;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentaHistorialFilterDTO {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;

    private EstadoVenta estado;

    private String keywordCliente;

    private String keywordVendedor;
}
