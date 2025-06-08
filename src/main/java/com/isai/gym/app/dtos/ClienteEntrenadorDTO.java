package com.isai.gym.app.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEntrenadorDTO {

    private Long id;

    @NotNull(message = "Debe seleccionar un usuario")
    private Long usuarioId;

    @NotNull(message = "Debe seleccionar un entrenador")
    private Long entrenadorId;

    @NotNull(message = "La fecha de inicio no puede ser nula")
    private LocalDate fechaInicio;

    @FutureOrPresent(message = "La fecha de fin no puede ser en el pasado")
    private LocalDate fechaFin;

}