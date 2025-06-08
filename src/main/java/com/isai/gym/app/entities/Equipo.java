package com.isai.gym.app.entities;

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

@Entity
@Table(name = "equipo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del equipo no puede estar vacío")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "La cantidad disponible no puede ser nula")
    @Min(value = 0, message = "La cantidad disponible no puede ser negativa")
    @Column(name = "cantidad_disponible", nullable = false)
    private Integer cantidadDisponible;

    @NotNull(message = "El estado de mantenimiento no puede ser nulo")
    @Column(name = "en_mantenimiento", nullable = false)
    private Boolean enMantenimiento;

    @NotNull(message = "El tipo de equipo no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoEquipo tipo;

    @PastOrPresent(message = "La fecha de adquisición no puede ser en el futuro")
    @Column(name = "fecha_adquisicion")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaAdquisicion;

    @PastOrPresent(message = "La fecha del último mantenimiento no puede ser en el futuro")
    @Column(name = "fecha_ultimo_mantenimiento")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaUltimoMantenimiento;

    @FutureOrPresent(message = "La próxima fecha de mantenimiento no puede ser en el pasado")
    @Column(name = "proxima_fecha_mantenimiento")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate proximaFechaMantenimiento;

    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    @Column(name = "ubicacion")
    private String ubicacion;

    @NotNull(message = "El estado del equipo no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoEquipo estado;

    @Size(max = 100, message = "El número de serie no puede exceder 100 caracteres")
    @Column(name = "numero_serie", unique = true)
    private String numeroSerie;

    @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
    @Column(name = "marca")
    private String marca;

    @Size(max = 100, message = "El modelo no puede exceder 100 caracteres")
    @Column(name = "modelo")
    private String modelo;

    private String rutaImagen;

    @Transient
    private MultipartFile foto;

    @PrePersist
    protected void onCreate() {
        if (cantidadDisponible == null) {
            cantidadDisponible = 0;
        }
        if (enMantenimiento == null) {
            enMantenimiento = false;
        }
        if (estado == null) {
            estado = EstadoEquipo.DISPONIBLE;
        }
    }
}
