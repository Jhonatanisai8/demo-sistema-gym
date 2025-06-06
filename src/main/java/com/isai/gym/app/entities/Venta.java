package com.isai.gym.app.entities;

import com.isai.gym.app.enums.EstadoVenta;
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
@Table(name = "ventas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha de venta no puede ser nula")
    @PastOrPresent(message = "La fecha de venta no puede ser en el futuro")
    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;

    @NotNull(message = "El monto total no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto total no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El monto total debe tener hasta 10 dígitos enteros y 2 decimales")
    @Column(name = "monto_total", nullable = false)
    private BigDecimal montoTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id") // usuario_id puede ser nulo si es una venta anónima
    private Usuario usuario;

    @NotNull(message = "El estado de la venta no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoVenta estado;

    @NotBlank(message = "El método de pago no puede estar vacío")
    @Size(max = 50, message = "El método de pago no puede exceder 50 caracteres")
    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;

    @DecimalMin(value = "0.0", inclusive = true, message = "El descuento no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El descuento debe tener hasta 10 dígitos enteros y 2 decimales")
    @Column(name = "descuento")
    private BigDecimal descuento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id") // vendedor_id puede ser nulo (ej. si no se registra)
    private Usuario vendedor;

    // --- Relaciones ---
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ItemVenta> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (fechaVenta == null) {
            fechaVenta = LocalDateTime.now();
        }
        if (montoTotal == null) {
            montoTotal = BigDecimal.ZERO;
        }
        if (descuento == null) {
            descuento = BigDecimal.ZERO;
        }
        if (estado == null) {
            estado = EstadoVenta.PENDIENTE;
        }
    }
}
