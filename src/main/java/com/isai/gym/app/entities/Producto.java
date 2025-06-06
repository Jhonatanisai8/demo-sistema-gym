package com.isai.gym.app.entities;

import com.isai.gym.app.enums.CategoriaProducto;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener hasta 10 dígitos enteros y 2 decimales")
    @Column(name = "precio", nullable = false)
    private BigDecimal precio;

    @NotNull(message = "El stock no puede ser nulo")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @NotNull(message = "La categoría no puede ser nula")
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private CategoriaProducto categoria;

    @Size(max = 100, message = "El código de barras no puede exceder 100 caracteres")
    @Column(name = "codigo_barras", unique = true)
    private String codigoBarras;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    @NotNull(message = "El estado activo no puede ser nulo")
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @NotNull(message = "La fecha de creación no puede ser nula")
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Size(max = 255, message = "El proveedor no puede exceder 255 caracteres")
    @Column(name = "proveedor")
    private String proveedor;

    @FutureOrPresent(message = "La fecha de vencimiento no puede ser en el pasado")
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    // --- Relaciones ---
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ItemVenta> itemsVenta = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
        if (stock == null) {
            stock = 0;
        }
        if (stockMinimo == null) {
            stockMinimo = 0;
        }
    }
}
