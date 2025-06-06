package com.isai.gym.app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El usuario que realiza el pago no puede ser nulo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotBlank(message = "El concepto de pago no puede estar vacío")
    @Size(max = 50, message = "El concepto no puede exceder 50 caracteres")
    @Column(name = "concepto", nullable = false)
    private String concepto;

    @NotNull(message = "El monto no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El monto debe tener hasta 10 dígitos enteros y 2 decimales")
    @Column(name = "monto", nullable = false)
    private BigDecimal monto;

    @NotNull(message = "La fecha de pago no puede ser nula")
    @PastOrPresent(message = "La fecha de pago no puede ser en el futuro")
    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @NotBlank(message = "El método de pago no puede estar vacío")
    @Size(max = 50, message = "El método de pago no puede exceder 50 caracteres")
    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;

    @Column(name = "referencia_id")
    private Long referenciaId; // ID de la entidad a la que se refiere el pago (MembresiaCliente.id, Venta.id)

    @NotBlank(message = "El estado del pago no puede estar vacío")
    @Size(max = 50, message = "El estado no puede exceder 50 caracteres")
    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @PrePersist
    protected void onCreate() {
        if (fechaPago == null) {
            fechaPago = LocalDateTime.now();
        }
    }
}
