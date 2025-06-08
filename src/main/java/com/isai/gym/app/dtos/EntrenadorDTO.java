package com.isai.gym.app.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntrenadorDTO {

    private Long id;

    @NotBlank(message = "El nombre del entrenador no puede estar vacío")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String nombre;

    @Size(max = 255, message = "La especialidad no puede exceder 255 caracteres")
    private String especialidad;

    @Size(max = 50, message = "El teléfono no puede exceder 50 caracteres")
    private String telefono;

    @Email(message = "Debe ser un formato de email válido")
    @Size(max = 255, message = "El email no puede exceder 255 caracteres")
    private String email;

    @PastOrPresent(message = "La fecha de contratación no puede ser en el futuro")
    @DateTimeFormat(pattern = "yyyy-dd-MM")
    private LocalDate fechaContratacion;

    @NotNull(message = "El estado activo no puede ser nulo")
    private Boolean activo;

    @NotNull(message = "La tarifa por sesión no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "La tarifa no puede ser negativa")
    @Digits(integer = 10, fraction = 2, message = "La tarifa debe tener hasta 10 dígitos enteros y 2 decimales")
    private BigDecimal tarifaPorSesion;

    @Size(max = 2000, message = "Las certificaciones no pueden exceder 2000 caracteres")
    private String certificaciones;

    @Size(max = 2000, message = "El horario disponible no puede exceder 2000 caracteres")
    private String horarioDisponible;

    private MultipartFile fotoNueva;

    private String rutaImagenActual;
}