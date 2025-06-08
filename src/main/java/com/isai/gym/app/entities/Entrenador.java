package com.isai.gym.app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "entrenadores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Entrenador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del entrenador no puede estar vacío")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Size(max = 255, message = "La especialidad no puede exceder 255 caracteres")
    @Column(name = "especialidad")
    private String especialidad;

    @Size(max = 50, message = "El teléfono no puede exceder 50 caracteres")
    @Column(name = "telefono")
    private String telefono;

    @Email(message = "Debe ser un formato de email válido")
    @Column(name = "email")
    private String email;

    @PastOrPresent(message = "La fecha de contratación no puede ser en el futuro")
    @Column(name = "fecha_contratacion")
    private LocalDate fechaContratacion;

    @NotNull(message = "El estado activo no puede ser nulo")
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @NotNull(message = "La tarifa por sesión no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "La tarifa no puede ser negativa")
    @Digits(integer = 10, fraction = 2, message = "La tarifa debe tener hasta 10 dígitos enteros y 2 decimales")
    @Column(name = "tarifa_por_sesion", nullable = false)
    private BigDecimal tarifaPorSesion;

    @Column(name = "certificaciones", columnDefinition = "TEXT")
    private String certificaciones;

    @Column(name = "horario_disponible", columnDefinition = "TEXT")
    private String horarioDisponible;


    private String rutaImagen;

    @Transient
    private MultipartFile foto;

    // --- Relaciones ---
    @OneToMany(mappedBy = "entrenador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClienteEntrenador> clientesEntrenador = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (activo == null) {
            activo = true;
        }
    }
}
