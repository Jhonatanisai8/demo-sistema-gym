package com.isai.gym.app.services.impl;

import com.isai.gym.app.dtos.ItemVentaDTO;
import com.isai.gym.app.dtos.VentaDTO;
import com.isai.gym.app.entities.ItemVenta;
import com.isai.gym.app.entities.Producto;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.entities.Venta;
import com.isai.gym.app.enums.EstadoVenta;
import com.isai.gym.app.repository.ItemVentaRepository;
import com.isai.gym.app.repository.ProductoRepository;
import com.isai.gym.app.repository.UsuarioRepository;
import com.isai.gym.app.repository.VentaRepository;
import com.isai.gym.app.services.ProductoService;
import com.isai.gym.app.services.VentaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional; // Necesario para Optional.ofNullable

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ItemVentaRepository itemVentaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoService productoService;

    public VentaServiceImpl(VentaRepository ventaRepository,
                            ItemVentaRepository itemVentaRepository,
                            ProductoRepository productoRepository,
                            UsuarioRepository usuarioRepository,
                            ProductoService productoService) {
        this.ventaRepository = ventaRepository;
        this.itemVentaRepository = itemVentaRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoService = productoService;
    }

    // --- Métodos de Conversión MANUAL ---
    private Venta convertToEntity(VentaDTO ventaDTO) {
        Venta venta = new Venta();
        venta.setId(ventaDTO.getId());
        venta.setFechaVenta(ventaDTO.getFechaVenta() != null ? ventaDTO.getFechaVenta() : LocalDateTime.now());
        venta.setMontoTotal(ventaDTO.getMontoTotal());
        venta.setEstado(ventaDTO.getEstado() != null ? ventaDTO.getEstado() : EstadoVenta.PENDIENTE); // Default
        venta.setMetodoPago(ventaDTO.getMetodoPago());
        venta.setDescuento(ventaDTO.getDescuento() != null ? ventaDTO.getDescuento() : BigDecimal.ZERO);

        // Cliente (Usuario)
        Optional.ofNullable(ventaDTO.getUsuarioId())
                .flatMap(usuarioRepository::findById)
                .ifPresent(venta::setUsuario);

        // Vendedor (Usuario)
        Optional.ofNullable(ventaDTO.getVendedorId())
                .flatMap(usuarioRepository::findById)
                .ifPresent(venta::setVendedor);

        // Los items se manejarán por separado en el método registrarVenta
        return venta;
    }

    private VentaDTO convertToDto(Venta venta) {
        VentaDTO ventaDTO = new VentaDTO();
        ventaDTO.setId(venta.getId());
        ventaDTO.setFechaVenta(venta.getFechaVenta());
        ventaDTO.setMontoTotal(venta.getMontoTotal());
        ventaDTO.setEstado(venta.getEstado());
        ventaDTO.setMetodoPago(venta.getMetodoPago());
        ventaDTO.setDescuento(venta.getDescuento());

        if (venta.getUsuario() != null) {
            ventaDTO.setUsuarioId(venta.getUsuario().getId());
            ventaDTO.setNombreCliente(venta.getUsuario().getNombreCompleto());
        }
        if (venta.getVendedor() != null) {
            ventaDTO.setVendedorId(venta.getVendedor().getId());
            ventaDTO.setNombreVendedor(venta.getVendedor().getNombreCompleto());
        }

        // Mapear los ItemVenta a ItemVentaDTO
        ventaDTO.setItems(venta.getItems().stream()
                .map(this::convertItemVentaToDto)
                .collect(Collectors.toList()));

        return ventaDTO;
    }

    private ItemVenta convertItemVentaToEntity(ItemVentaDTO itemDTO) {
        ItemVenta item = new ItemVenta();
        item.setId(itemDTO.getId());
        item.setCantidad(itemDTO.getCantidad());
        item.setPrecioUnitario(itemDTO.getPrecioUnitario());
        return item;
    }

    private ItemVentaDTO convertItemVentaToDto(ItemVenta item) {
        ItemVentaDTO itemDTO = new ItemVentaDTO();
        itemDTO.setId(item.getId());
        itemDTO.setProductoId(item.getProducto().getId());
        itemDTO.setNombreProducto(item.getProducto().getNombre());
        itemDTO.setCantidad(item.getCantidad());
        itemDTO.setPrecioUnitario(item.getPrecioUnitario());
        itemDTO.setSubtotal(item.getSubtotal());
        return itemDTO;
    }

    @Override
    @Transactional
    public VentaDTO registrarVenta(VentaDTO ventaDTO) {
        Usuario vendedor = usuarioRepository.findById(ventaDTO.getVendedorId())
                .orElseThrow(() -> new IllegalArgumentException("Vendedor no encontrado con ID: " + ventaDTO.getVendedorId()));
        Usuario cliente = null;
        if (ventaDTO.getUsuarioId() != null) {
            cliente = usuarioRepository.findById(ventaDTO.getUsuarioId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + ventaDTO.getUsuarioId()));
        }
        Venta venta = new Venta();
        venta.setId(ventaDTO.getId());
        venta.setFechaVenta(ventaDTO.getFechaVenta() != null ? ventaDTO.getFechaVenta() : LocalDateTime.now());
        venta.setEstado(ventaDTO.getEstado() != null ? ventaDTO.getEstado() : EstadoVenta.COMPLETADA);
        venta.setMetodoPago(ventaDTO.getMetodoPago());
        venta.setDescuento(ventaDTO.getDescuento() != null ? ventaDTO.getDescuento() : BigDecimal.ZERO);
        venta.setVendedor(vendedor);
        venta.setUsuario(cliente);
        BigDecimal totalCalculado = BigDecimal.ZERO;
        if (ventaDTO.getItems() == null || ventaDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un producto.");
        }

        // Lista para los items de venta que se persistirán
        List<ItemVenta> itemsVentaParaPersistir = new java.util.ArrayList<>();

        for (ItemVentaDTO itemDTO : ventaDTO.getItems()) {
            Producto producto = productoRepository.findById(itemDTO.getProductoId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + itemDTO.getProductoId()));

            if (itemDTO.getCantidad() == null || itemDTO.getCantidad() <= 0) {
                throw new IllegalArgumentException("Cantidad inválida para el producto: " + producto.getNombre());
            }

            // Verificar stock antes de procesar cualquier cosa
            if (producto.getStock() < itemDTO.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre() + ". Disponible: " + producto.getStock() + ", Solicitado: " + itemDTO.getCantidad());
            }

            ItemVenta itemVenta = convertItemVentaToEntity(itemDTO);
            itemVenta.setProducto(producto);
            itemVenta.setPrecioUnitario(producto.getPrecio()); // Usar el precio actual del producto del inventario

            // El subtotal se calculará en el @PrePersist/@PreUpdate de ItemVenta, pero lo precalculamos para el total de la venta
            itemVenta.setSubtotal(itemVenta.getPrecioUnitario().multiply(BigDecimal.valueOf(itemVenta.getCantidad())));

            totalCalculado = totalCalculado.add(itemVenta.getSubtotal());
            itemsVentaParaPersistir.add(itemVenta);
        }

        // Aplicar descuento
        BigDecimal montoFinalConDescuento = totalCalculado.subtract(venta.getDescuento());
        if (montoFinalConDescuento.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El descuento no puede hacer que el monto total sea negativo.");
        }
        venta.setMontoTotal(montoFinalConDescuento);

        // Guardar la venta principal primero
        Venta savedVenta = ventaRepository.save(venta);

        // Asociar y guardar los items de venta, y decrementar stock
        for (ItemVenta item : itemsVentaParaPersistir) {
            savedVenta.getItems().add(item); // Añadir al set/list de la venta
            item.setVenta(savedVenta);       // Establecer la relación bidireccional
            itemVentaRepository.save(item);  // Guardar el item

            // Decrementar stock utilizando el servicio de producto
            productoService.decrementarStock(item.getProducto().getId(), item.getCantidad());
        }

        return convertToDto(savedVenta);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaDTO obtenerVentaPorId(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + id));
        return convertToDto(venta);
    }

    @Override
    @Transactional
    public void cancelarVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + id));

        if (venta.getEstado() == EstadoVenta.COMPLETADA || venta.getEstado() == EstadoVenta.PENDIENTE) {
            venta.setEstado(EstadoVenta.CANCELADA);
            ventaRepository.save(venta);

            // Revertir stock de los productos
            for (ItemVenta item : venta.getItems()) {
                productoService.incrementarStock(item.getProducto().getId(), item.getCantidad());
            }
        } else {
            throw new IllegalStateException("No se puede cancelar una venta en estado: " + venta.getEstado());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaDTO> obtenerTodasLasVentas(Pageable pageable) {
        return ventaRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaDTO> buscarVentas(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return obtenerTodasLasVentas(pageable);
        }
        return ventaRepository.buscarVentasPorKeyword(keyword, pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaDTO> obtenerVentasPorRangoDeFechas(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return ventaRepository.findByFechaVentaBetween(start, end, pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaDTO> obtenerVentasPorCliente(Long usuarioId, Pageable pageable) {
        return ventaRepository.findByUsuarioId(usuarioId, pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaDTO> obtenerVentasPorEstado(EstadoVenta estado, Pageable pageable) {
        return ventaRepository.findByEstado(estado, pageable)
                .map(this::convertToDto);
    }
}