package com.isai.gym.app.services.impl;

import com.isai.gym.app.dtos.CarritoDTO;
import com.isai.gym.app.dtos.CarritoItemDTO;
import com.isai.gym.app.dtos.ItemVentaDTO;
import com.isai.gym.app.dtos.VentaDTO;
import com.isai.gym.app.entities.*;
import com.isai.gym.app.enums.EstadoPago;
import com.isai.gym.app.enums.EstadoVenta;
import com.isai.gym.app.enums.MetodoPago;
import com.isai.gym.app.repository.ItemVentaRepository;
import com.isai.gym.app.repository.PagoRepository;
import com.isai.gym.app.repository.ProductoRepository;
import com.isai.gym.app.repository.UsuarioRepository;
import com.isai.gym.app.repository.VentaRepository;
import com.isai.gym.app.services.VentaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ItemVentaRepository itemVentaRepository;
    private final PagoRepository pagoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public VentaServiceImpl(VentaRepository ventaRepository, ItemVentaRepository itemVentaRepository, PagoRepository pagoRepository, ProductoRepository productoRepository, UsuarioRepository usuarioRepository) {
        this.ventaRepository = ventaRepository;
        this.itemVentaRepository = itemVentaRepository;
        this.pagoRepository = pagoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public Venta crearVenta(CarritoDTO carrito, Usuario usuario, MetodoPago metodoPago) {
        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setMontoTotal(carrito.getTotal());
        venta.setEstado(EstadoVenta.COMPLETADA);
        venta.setMetodoPago(metodoPago.getDisplayName());

        Venta ventaGuardada = ventaRepository.save(venta);

        for (CarritoItemDTO itemDTO : carrito.getItems()) {
            Producto producto = productoRepository.findById(itemDTO.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + itemDTO.getIdProducto()));

            if (producto.getStock() < itemDTO.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            ItemVenta itemVenta = new ItemVenta();
            itemVenta.setVenta(ventaGuardada);
            itemVenta.setProducto(producto);
            itemVenta.setCantidad(itemDTO.getCantidad());
            itemVenta.setPrecioUnitario(itemDTO.getPrecioUnitario());
            itemVenta.setSubtotal(itemDTO.getSubtotal());
            itemVentaRepository.save(itemVenta);

            // Actualizar stock
            producto.setStock(producto.getStock() - itemDTO.getCantidad());
            productoRepository.save(producto);
        }

        // Registrar el pago
        Pago pago = new Pago();
        pago.setUsuario(usuario);
        pago.setMonto(carrito.getTotal());
        pago.setFechaPago(LocalDateTime.now());
        pago.setMetodoPago(metodoPago.getDisplayName());
        pago.setEstado(EstadoPago.COMPLETADO.name());
        pago.setConcepto("Compra en tienda online");
        pago.setReferenciaId(ventaGuardada.getId());
        pagoRepository.save(pago);

        return ventaGuardada;
    }

    @Override
    @Transactional
    public VentaDTO registrarVenta(VentaDTO ventaDTO) {
        Venta venta = convertToEntity(ventaDTO);
        Venta ventaGuardada = ventaRepository.save(venta);
        // Aquí podrías manejar los items de la venta si vienen en el DTO
        return convertToDto(ventaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaDTO obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public void cancelarVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + id));

        if (venta.getEstado() == EstadoVenta.CANCELADA) {
            throw new IllegalStateException("La venta ya ha sido cancelada.");
        }

        venta.setEstado(EstadoVenta.CANCELADA);

        // Revertir el stock de los productos
        for (ItemVenta item : venta.getItems()) {
            Producto producto = item.getProducto();
            producto.setStock(producto.getStock() + item.getCantidad());
            productoRepository.save(producto);
        }

        ventaRepository.save(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaDTO> obtenerTodasLasVentas(Pageable pageable) {
        return ventaRepository.findAll(pageable).map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaDTO> buscarVentas(String keyword, Pageable pageable) {
        return ventaRepository.buscarVentasPorKeyword(keyword, pageable).map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaDTO> obtenerVentasPorRangoDeFechas(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return ventaRepository.findByFechaVentaBetween(start, end, pageable).map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaDTO> obtenerVentasPorCliente(Long usuarioId, Pageable pageable) {
        return ventaRepository.findByUsuarioId(usuarioId, pageable).map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaDTO> obtenerVentasPorEstado(EstadoVenta estado, Pageable pageable) {
        return ventaRepository.findByEstado(estado, pageable).map(this::convertToDto);
    }

    // --- Métodos de Conversión --- 

    private VentaDTO convertToDto(Venta venta) {
        VentaDTO dto = new VentaDTO();
        dto.setId(venta.getId());
        dto.setFechaVenta(venta.getFechaVenta());
        dto.setMontoTotal(venta.getMontoTotal());
        dto.setEstado(venta.getEstado());
        dto.setMetodoPago(venta.getMetodoPago());
        dto.setDescuento(venta.getDescuento());

        if (venta.getUsuario() != null) {
            dto.setUsuarioId(venta.getUsuario().getId());
            dto.setNombreCliente(venta.getUsuario().getNombreCompleto());
        }

        if (venta.getVendedor() != null) {
            dto.setVendedorId(venta.getVendedor().getId());
            dto.setNombreVendedor(venta.getVendedor().getNombreCompleto());
        }

        dto.setItems(venta.getItems().stream()
                .map(this::convertItemVentaToDto)
                .collect(Collectors.toList()));

        return dto;
    }

    private ItemVentaDTO convertItemVentaToDto(ItemVenta item) {
        ItemVentaDTO dto = new ItemVentaDTO();
        dto.setId(item.getId());
        dto.setProductoId(item.getProducto().getId());
        dto.setNombreProducto(item.getProducto().getNombre());
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }

    private Venta convertToEntity(VentaDTO dto) {
        Venta venta = new Venta();
        venta.setId(dto.getId());
        venta.setFechaVenta(dto.getFechaVenta());
        venta.setMontoTotal(dto.getMontoTotal());
        venta.setEstado(dto.getEstado());
        venta.setMetodoPago(dto.getMetodoPago());
        venta.setDescuento(dto.getDescuento());

        if (dto.getUsuarioId() != null) {
            Usuario cliente = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + dto.getUsuarioId()));
            venta.setUsuario(cliente);
        }

        if (dto.getVendedorId() != null) {
            Usuario vendedor = usuarioRepository.findById(dto.getVendedorId())
                .orElseThrow(() -> new EntityNotFoundException("Vendedor no encontrado con ID: " + dto.getVendedorId()));
            venta.setVendedor(vendedor);
        }
        
        return venta;
    }
}