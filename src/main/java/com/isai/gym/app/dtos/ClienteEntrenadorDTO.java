package com.isai.gym.app.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;

    @FutureOrPresent(message = "La fecha de fin no puede ser en el pasado")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
     private LocalDate fechaFin;

}