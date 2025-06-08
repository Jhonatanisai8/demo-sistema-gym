package com.isai.gym.app.entities;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Entity
@Table(name = "clientes_entrenadores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEntrenador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha de inicio de la asignación no puede ser nula")
    @PastOrPresent(message = "La fecha de inicio no puede ser en el futuro")
    @Column(name = "fecha_inicio", nullable = false)
    @DateTimeFormat(pattern = "yyyy-dd-MM")
    private LocalDate fechaInicio;

    @FutureOrPresent(message = "La fecha de fin no puede ser en el pasado")
    @Column(name = "fecha_fin")
    @DateTimeFormat(pattern = "yyyy-dd-MM")
    private LocalDate fechaFin;

    @NotNull(message = "El usuario no puede ser nulo para la asignación")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull(message = "El entrenador no puede ser nulo para la asignación")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrenador_id", nullable = false)
    private Entrenador entrenador;
}
