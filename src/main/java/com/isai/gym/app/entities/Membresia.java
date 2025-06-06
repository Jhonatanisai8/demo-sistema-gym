package com.isai.gym.app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "membresias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Membresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la membresía no puede estar vacío")
    @Size(max = 255, message = "El nombre de la membresía no puede exceder 255 caracteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener hasta 10 dígitos enteros y 2 decimales")
    @Column(name = "precio", nullable = false)
    private BigDecimal precio;

    @NotNull(message = "La duración en días no puede ser nula")
    @Min(value = 1, message = "La duración debe ser al menos 1 día")
    @Column(name = "duracion_dias", nullable = false)
    private Integer duracionDias;

    @NotNull(message = "El estado de actividad no puede ser nulo")
    @Column(name = "activa", nullable = false)
    private Boolean activa;

    @NotNull(message = "La fecha de creación no puede ser nula")
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "beneficios", columnDefinition = "TEXT")
    private String beneficios;

    @Min(value = 0, message = "El límite de accesos por día no puede ser negativo")
    @Column(name = "limite_accesos_dia")
    private Integer limiteAccesosDia;

    // --- Relaciones ---
    @OneToMany(mappedBy = "membresia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MembresiaCliente> membresiasCliente = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (activa == null) {
            activa = true;
        }
    }
}
