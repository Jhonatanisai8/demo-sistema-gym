package com.isai.gym.app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "accesos_gimnasio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccesoGimnasio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El usuario no puede ser nulo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull(message = "La fecha y hora de entrada no puede ser nula")
    @PastOrPresent(message = "La fecha y hora de entrada no puede ser en el futuro")
    @Column(name = "fecha_hora_entrada", nullable = false)
    private LocalDateTime fechaHoraEntrada;

    @Column(name = "fecha_hora_salida")
    private LocalDateTime fechaHoraSalida;

    @NotNull(message = "El estado activo no puede ser nulo")
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @PrePersist
    protected void onCreate() {
        if (fechaHoraEntrada == null) {
            fechaHoraEntrada = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }
}
