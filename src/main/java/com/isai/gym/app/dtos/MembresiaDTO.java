package com.isai.gym.app.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MembresiaDTO {
    private Long id;

    @NotBlank(message = "El nombre de la membresía no puede estar vacío")
    @Size(max = 255, message = "El nombre de la membresía no puede exceder 255 caracteres")
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres") // Ejemplo de tamaño
    private String descripcion;

    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener hasta 10 dígitos enteros y 2 decimales")
    private BigDecimal precio;

    @NotNull(message = "La duración en días no puede ser nula")
    @Min(value = 1, message = "La duración debe ser al menos 1 día")
    private Integer duracionDias;

    @NotNull(message = "El estado de actividad no puede ser nulo")
    private Boolean activa;

    @Size(max = 2000, message = "Los beneficios no pueden exceder 2000 caracteres")
    private String beneficios;

    @Min(value = 0, message = "El límite de accesos por día no puede ser negativo")
    private Integer limiteAccesosDia;

}
