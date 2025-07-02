package com.isai.gym.app.services;


import com.isai.gym.app.dtos.ProductoDTO;
import com.isai.gym.app.dtos.ProductoTiendaDTO;
import com.isai.gym.app.enums.CategoriaProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    ProductoDTO crearProducto(ProductoDTO productoDTO, MultipartFile foto) throws Exception;

    ProductoDTO actualizarProducto(Long id, ProductoDTO productoDTO, MultipartFile foto) throws Exception;

    void eliminarProducto(Long id);

    ProductoDTO obtenerProductoPorId(Long id);

    Page<ProductoDTO> obtenerTodosLosProductos(Pageable pageable);

    Page<ProductoDTO> buscarProductos(String keyword, Pageable pageable);

    Page<ProductoDTO> obtenerProductosActivos(Pageable pageable);

    Page<ProductoDTO> buscarProductosActivos(String keyword, Pageable pageable);

    Page<ProductoDTO> obtenerProductosPorCategoriaActivos(CategoriaProducto categoria, Pageable pageable);

    Optional<ProductoDTO> obtenerProductoPorCodigoBarras(String codigoBarras);

    void decrementarStock(Long productoId, Integer cantidad);

    void incrementarStock(Long productoId, Integer cantidad);

    List<ProductoDTO> obtenerTodosLosProductosActivos();

    List<ProductoTiendaDTO> obtenerTodosLosProductosActivosTienda();

    List<ProductoTiendaDTO> obtenerProductosActivosPorCategoria(CategoriaProducto categoria);

    List<ProductoTiendaDTO> buscarProductosActivos(String keyword);

    Optional<ProductoTiendaDTO> obtenerProductoActivoPorId(Long id);
}
