package com.isai.gym.app.services.impl;

import com.isai.gym.app.dtos.ProductoDTO;
import com.isai.gym.app.entities.Producto;
import com.isai.gym.app.enums.CategoriaProducto;
import com.isai.gym.app.exceptions.WarehouseException;
import com.isai.gym.app.repository.ProductoRepository;
import com.isai.gym.app.services.AlmacenServicio;
import com.isai.gym.app.services.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final AlmacenServicio almacenServicio;

    public ProductoServiceImpl(ProductoRepository productoRepository, AlmacenServicio almacenServicio) {
        this.productoRepository = productoRepository;
        this.almacenServicio = almacenServicio;
    }

    private Producto convertToEntity(ProductoDTO productoDTO) {
        Producto producto = new Producto();
        producto.setId(productoDTO.getId());
        producto.setNombre(productoDTO.getNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setStock(productoDTO.getStock());
        producto.setCategoria(productoDTO.getCategoria());
        producto.setCodigoBarras(productoDTO.getCodigoBarras());
        producto.setStockMinimo(productoDTO.getStockMinimo());
        producto.setActivo(productoDTO.getActivo());
        if (productoDTO.getFechaCreacion() != null) {
            producto.setFechaCreacion(productoDTO.getFechaCreacion());
        }
        producto.setProveedor(productoDTO.getProveedor());
        producto.setFechaVencimiento(productoDTO.getFechaVencimiento());
        producto.setRutaImagen(productoDTO.getRutaImagen());
        return producto;
    }

    private ProductoDTO convertToDto(Producto producto) {
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setId(producto.getId());
        productoDTO.setNombre(producto.getNombre());
        productoDTO.setDescripcion(producto.getDescripcion());
        productoDTO.setPrecio(producto.getPrecio());
        productoDTO.setStock(producto.getStock());
        productoDTO.setCategoria(producto.getCategoria());
        productoDTO.setCodigoBarras(producto.getCodigoBarras());
        productoDTO.setStockMinimo(producto.getStockMinimo());
        productoDTO.setActivo(producto.getActivo());
        productoDTO.setFechaCreacion(producto.getFechaCreacion());
        productoDTO.setProveedor(producto.getProveedor());
        productoDTO.setFechaVencimiento(producto.getFechaVencimiento());
        productoDTO.setRutaImagen(producto.getRutaImagen());
        return productoDTO;
    }

    @Override
    @Transactional
    public ProductoDTO crearProducto(ProductoDTO productoDTO, MultipartFile foto) throws Exception {
        productoRepository.findByNombreIgnoreCase(productoDTO.getNombre())
                .ifPresent(p -> {
                    throw new IllegalArgumentException("Ya existe un producto con el nombre: " + productoDTO.getNombre());
                });
        if (productoDTO.getCodigoBarras() != null && !productoDTO.getCodigoBarras().isEmpty()) {
            productoRepository.findByCodigoBarras(productoDTO.getCodigoBarras())
                    .ifPresent(p -> {
                        throw new IllegalArgumentException("Ya existe un producto con el código de barras: " + productoDTO.getCodigoBarras());
                    });
        }

        Producto producto = convertToEntity(productoDTO);

        if (foto != null && !foto.isEmpty()) {
            try {
                String fileName = almacenServicio.almacenarArchivo(foto);
                producto.setRutaImagen(fileName);
            } catch (WarehouseException e) {
                throw new Exception("Error al almacenar la imagen del producto: " + e.getMessage(), e);
            }
        } else {
            producto.setRutaImagen(null);
        }

        Producto savedProducto = productoRepository.save(producto);
        return convertToDto(savedProducto);
    }

    @Override
    @Transactional
    public ProductoDTO actualizarProducto(Long id, ProductoDTO productoDTO, MultipartFile foto) throws Exception {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));

        productoRepository.findByNombreIgnoreCase(productoDTO.getNombre())
                .ifPresent(p -> {
                    if (!p.getId().equals(id)) {
                        throw new IllegalArgumentException("Ya existe otro producto con el nombre: " + productoDTO.getNombre());
                    }
                });
        if (productoDTO.getCodigoBarras() != null && !productoDTO.getCodigoBarras().isEmpty()) {
            productoRepository.findByCodigoBarras(productoDTO.getCodigoBarras())
                    .ifPresent(p -> {
                        if (!p.getId().equals(id)) {
                            throw new IllegalArgumentException("Ya existe otro producto con el código de barras: " + productoDTO.getCodigoBarras());
                        }
                    });
        }

        productoExistente.setNombre(productoDTO.getNombre());
        productoExistente.setDescripcion(productoDTO.getDescripcion());
        productoExistente.setPrecio(productoDTO.getPrecio());
        productoExistente.setStock(productoDTO.getStock());
        productoExistente.setCategoria(productoDTO.getCategoria());
        productoExistente.setCodigoBarras(productoDTO.getCodigoBarras());
        productoExistente.setStockMinimo(productoDTO.getStockMinimo());
        productoExistente.setActivo(productoDTO.getActivo());
        productoExistente.setProveedor(productoDTO.getProveedor());
        productoExistente.setFechaVencimiento(productoDTO.getFechaVencimiento());

        if (foto != null && !foto.isEmpty()) {
            try {
                if (productoExistente.getRutaImagen() != null && !productoExistente.getRutaImagen().isEmpty()) {
                    almacenServicio.eliminarArchivo(productoExistente.getRutaImagen());
                }
                String fileName = almacenServicio.almacenarArchivo(foto);
                productoExistente.setRutaImagen(fileName);
            } catch (WarehouseException e) {
                throw new Exception("Error al actualizar la imagen del producto: " + e.getMessage(), e);
            }
        } else if (productoDTO.getRutaImagen() == null || productoDTO.getRutaImagen().isEmpty()) {
            if (productoExistente.getRutaImagen() != null && !productoExistente.getRutaImagen().isEmpty()) {
                almacenServicio.eliminarArchivo(productoExistente.getRutaImagen());
            }
            productoExistente.setRutaImagen(null);
        }
        Producto updatedProducto = productoRepository.save(productoExistente);
        return convertToDto(updatedProducto);
    }

    @Override
    @Transactional
    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));
        if (producto.getRutaImagen() != null && !producto.getRutaImagen().isEmpty()) {
            try {
                almacenServicio.eliminarArchivo(producto.getRutaImagen());
            } catch (WarehouseException e) {
                System.err.println("Error al eliminar la imagen del producto " + producto.getNombre() + ": " + e.getMessage());
            }
        }
        productoRepository.delete(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDTO obtenerProductoPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));
        return convertToDto(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> obtenerTodosLosProductos(Pageable pageable) {
        return productoRepository.findAll(pageable)
                .map(this::convertToDto); // Usar referencia al método de conversión
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> buscarProductos(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return obtenerTodosLosProductos(pageable);
        }
        return productoRepository.buscarProductosPorKeyword(keyword, pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> obtenerProductosActivos(Pageable pageable) {
        return productoRepository.findByActivoTrue(pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> buscarProductosActivos(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return obtenerProductosActivos(pageable);
        }
        return productoRepository.buscarProductosActivosPorKeyword(keyword, pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> obtenerProductosPorCategoriaActivos(CategoriaProducto categoria, Pageable pageable) {
        return productoRepository.findByCategoriaAndActivoTrue(categoria, pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductoDTO> obtenerProductoPorCodigoBarras(String codigoBarras) {
        return productoRepository.findByCodigoBarras(codigoBarras)
                .map(this::convertToDto);
    }

    @Override
    @Transactional
    public void decrementarStock(Long productoId, Integer cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a decrementar debe ser positiva.");
        }
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + productoId));
        if (producto.getActivo() == null || !producto.getActivo()) {
            throw new IllegalArgumentException("El producto " + producto.getNombre() + " no está activo para la venta.");
        }
        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException("No hay suficiente stock para el producto: " + producto.getNombre() + ". Disponible: " + producto.getStock() + ", Solicitado: " + cantidad);
        }
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void incrementarStock(Long productoId, Integer cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a incrementar debe ser positiva.");
        }
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + productoId));
        producto.setStock(producto.getStock() + cantidad);
        productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerTodosLosProductosActivos() {
        return productoRepository.findByActivoTrue(Pageable.unpaged()).getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}