package com.isai.gym.app.entities;

import com.isai.gym.app.enums.EstadoMembresia;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "membresias_cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembresiaCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha de inicio no puede ser nula")
    @PastOrPresent(message = "La fecha de inicio no puede ser en el futuro")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin no puede ser nula")
    @FutureOrPresent(message = "La fecha de fin no puede ser en el pasado")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @NotNull(message = "El estado activo no puede ser nulo")
    @Column(name = "activa", nullable = false)
    private Boolean activa;

    @NotNull(message = "El usuario no puede ser nulo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull(message = "La membresía no puede ser nula")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membresia_id", nullable = false)
    private Membresia membresia;

    @NotNull(message = "La fecha de compra no puede ser nula")
    @PastOrPresent(message = "La fecha de compra no puede ser en el futuro")
    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fechaCompra;

    @NotNull(message = "El estado de la membresía del cliente no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoMembresia estado;

    @NotNull(message = "El monto pagado no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto pagado no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El monto pagado debe tener hasta 10 dígitos enteros y 2 decimales")
    @Column(name = "monto_pagado", nullable = false)
    private BigDecimal montoPagado;

    @NotBlank(message = "El método de pago no puede estar vacío")
    @Size(max = 50, message = "El método de pago no puede exceder 50 caracteres")
    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;

    @PrePersist
    protected void onCreate() {
        if (fechaCompra == null) {
            fechaCompra = LocalDateTime.now();
        }
        if (activa == null) {
            activa = true;
        }
        if (estado == null) {
            estado = EstadoMembresia.ACTIVA;
        }
    }
}
