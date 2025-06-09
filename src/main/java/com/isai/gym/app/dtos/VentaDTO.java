package com.isai.gym.app.dtos;

import com.isai.gym.app.enums.EstadoVenta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDTO {

    private Long id;

    @NotNull(message = "La fecha de venta no puede ser nula")
    @PastOrPresent(message = "La fecha de venta no puede ser en el futuro")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaVenta;

    @NotNull(message = "El monto total no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = true, message = "El monto total no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El monto total debe tener hasta 10 dígitos enteros y 2 decimales")
    private BigDecimal montoTotal;

    private Long usuarioId;
    private String nombreCliente;

    @NotNull(message = "El estado de la venta no puede ser nulo")
    private EstadoVenta estado;

    @NotBlank(message = "El método de pago no puede estar vacío")
    @Size(max = 50, message = "El método de pago no puede exceder 50 caracteres")
    private String metodoPago;

    @DecimalMin(value = "0.0", inclusive = true, message = "El descuento no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El descuento debe tener hasta 10 dígitos enteros y 2 decimales")
    private BigDecimal descuento;

    @NotNull(message = "El vendedor es obligatorio")
    private Long vendedorId;
    private String nombreVendedor;

    @Valid
    @NotEmpty(message = "La venta debe contener al menos un producto")
    private List<ItemVentaDTO> items = new ArrayList<>();
}