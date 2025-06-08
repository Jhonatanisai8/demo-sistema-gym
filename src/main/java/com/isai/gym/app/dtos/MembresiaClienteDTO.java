package com.isai.gym.app.dtos;

import com.isai.gym.app.enums.EstadoMembresia;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembresiaClienteDTO {

    private Long id;

    @NotNull(message = "Debe seleccionar un usuario")
    private Long usuarioId;

    @NotNull(message = "Debe seleccionar un tipo de membresía")
    private Long membresiaId;

    @NotNull(message = "La fecha de inicio no puede ser nula")

    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin no puede ser nula")
    @FutureOrPresent(message = "La fecha de fin no puede ser en el pasado")
    private LocalDate fechaFin;

    @NotNull(message = "El estado activo no puede ser nulo")
    private Boolean activa;

    @NotNull(message = "El estado de la membresía del cliente no puede ser nulo")
    private EstadoMembresia estado;

    @NotNull(message = "El monto pagado no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto pagado no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El monto pagado debe tener hasta 10 dígitos enteros y 2 decimales")
    private BigDecimal montoPagado;

    @NotBlank(message = "El método de pago no puede estar vacío")
    @Size(max = 50, message = "El método de pago no puede exceder 50 caracteres")
    private String metodoPago;


}