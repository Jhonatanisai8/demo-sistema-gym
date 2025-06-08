package com.isai.gym.app.dtos;

import com.isai.gym.app.enums.EstadoEquipo;
import com.isai.gym.app.enums.TipoEquipo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipoDTO {

    private Long id;

    @NotBlank(message = "El nombre del equipo no puede estar vacío")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String nombre;

    @NotEmpty(message = "La descripcion no puede estar vacía.")
    private String descripcion;

    @NotNull(message = "La cantidad disponible no puede ser nula")
    @Min(value = 0, message = "La cantidad disponible no puede ser negativa")
    private Integer cantidadDisponible;

    @NotNull(message = "El estado de mantenimiento no puede ser nulo")
    private Boolean enMantenimiento;

    @NotNull(message = "El tipo de equipo no puede ser nulo")
    private TipoEquipo tipo;

    @PastOrPresent(message = "La fecha de adquisición no puede ser en el futuro")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaAdquisicion;

    @PastOrPresent(message = "La fecha del último mantenimiento no puede ser en el futuro")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaUltimoMantenimiento;

    @FutureOrPresent(message = "La próxima fecha de mantenimiento no puede ser en el pasado")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate proximaFechaMantenimiento;

    @NotEmpty
    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    private String ubicacion;

    @NotNull(message = "El estado del equipo no puede ser nulo")
    private EstadoEquipo estado;

    @NotEmpty
    @Size(max = 100, message = "El número de serie no puede exceder 100 caracteres")
    @Column(name = "numero_serie", unique = true)
    private String numeroSerie;

    @NotEmpty
    @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
    private String marca;

    @NotEmpty
    @Size(max = 100, message = "El modelo no puede exceder 100 caracteres")
    private String modelo;

    private String rutaImagen;

    private MultipartFile foto;

}
