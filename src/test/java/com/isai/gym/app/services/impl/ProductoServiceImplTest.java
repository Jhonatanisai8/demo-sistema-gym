package com.isai.gym.app.services.impl;

import com.isai.gym.app.dtos.ProductoDTO;
import com.isai.gym.app.entities.Producto;
import com.isai.gym.app.repository.ProductoRepository;
import com.isai.gym.app.services.AlmacenServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private AlmacenServicio almacenServicio;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private ProductoDTO productoDTO;
    private Producto producto;

    @BeforeEach
    void setUp() {
        productoDTO = new ProductoDTO();
        productoDTO.setId(1L);
        productoDTO.setNombre("Test Product");
        productoDTO.setCodigoBarras("123456789");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Test Product");
        producto.setCodigoBarras("123456789");
    }

    @Test
    void testCrearProducto_Exitoso() throws Exception {
        when(productoRepository.findByNombreIgnoreCase(any(String.class))).thenReturn(Optional.empty());
        when(productoRepository.findByCodigoBarras(any(String.class))).thenReturn(Optional.empty());
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoDTO result = productoService.crearProducto(productoDTO, null);

        assertNotNull(result);
        assertEquals(productoDTO.getNombre(), result.getNombre());
    }

    @Test
    void testCrearProducto_NombreDuplicado() {
        when(productoRepository.findByNombreIgnoreCase(any(String.class))).thenReturn(Optional.of(producto));

        assertThrows(IllegalArgumentException.class, () -> {
            productoService.crearProducto(productoDTO, null);
        });
    }

    @Test
    void testObtenerProductoPorId_Exitoso() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        ProductoDTO result = productoService.obtenerProductoPorId(1L);

        assertNotNull(result);
        assertEquals(producto.getId(), result.getId());
    }
}
