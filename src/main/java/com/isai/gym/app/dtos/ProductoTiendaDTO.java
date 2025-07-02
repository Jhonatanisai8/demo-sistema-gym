package com.isai.gym.app.dtos;

import com.isai.gym.app.enums.CategoriaProducto;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoTiendaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String rutaImagen;
    private CategoriaProducto categoria;
    private Boolean activo;
    //private MultipartFile foto;

    public boolean hayStock() {
        return this.stock != null && this.stock > 0;
    }
}
