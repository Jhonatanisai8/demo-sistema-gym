package com.isai.gym.app.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CarritoItemDTO {
    private Long idProducto;
    private String nombre;
    private String rutaImagen;
    private BigDecimal precioUnitario;
    private int cantidad;
    private BigDecimal subtotal;

    public CarritoItemDTO(Long idProducto, String nombre, String rutaImagen, BigDecimal precioUnitario, int cantidad) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.rutaImagen = rutaImagen;
        this.precioUnitario = precioUnitario;
        this.setCantidad(cantidad);
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        if (this.precioUnitario != null) {
            this.subtotal = this.precioUnitario.multiply(new BigDecimal(cantidad));
        }
    }
}
