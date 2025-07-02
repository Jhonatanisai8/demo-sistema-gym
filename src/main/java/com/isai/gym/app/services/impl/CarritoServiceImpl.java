package com.isai.gym.app.services.impl;

import com.isai.gym.app.dtos.CarritoDTO;
import com.isai.gym.app.dtos.CarritoItemDTO;
import com.isai.gym.app.entities.Producto;
import com.isai.gym.app.repository.ProductoRepository;
import com.isai.gym.app.services.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CarritoServiceImpl implements CarritoService {

    private final ProductoRepository productoRepository;
    private static final String CARRITO_SESSION_KEY = "carrito";

    public CarritoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public CarritoDTO getCarrito(HttpSession session) {
        CarritoDTO carrito = (CarritoDTO) session.getAttribute(CARRITO_SESSION_KEY);
        if (carrito == null) {
            carrito = new CarritoDTO();
            session.setAttribute(CARRITO_SESSION_KEY, carrito);
        }
        return carrito;
    }

    @Override
    public void agregarProducto(HttpSession session, Long idProducto, int cantidad) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        if (producto.getStock() < cantidad) {
            throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        CarritoDTO carrito = getCarrito(session);
        Optional<CarritoItemDTO> itemExistente = carrito.getItems().stream()
                .filter(item -> item.getIdProducto().equals(idProducto))
                .findFirst();

        if (itemExistente.isPresent()) {
            CarritoItemDTO item = itemExistente.get();
            int nuevaCantidad = item.getCantidad() + cantidad;
            if (producto.getStock() < nuevaCantidad) {
                throw new IllegalStateException("Stock insuficiente para la cantidad solicitada.");
            }
            item.setCantidad(nuevaCantidad);
        } else {
            CarritoItemDTO nuevoItem = new CarritoItemDTO(producto.getId(), producto.getNombre(),
                    producto.getRutaImagen(), producto.getPrecio(), cantidad);
            carrito.getItems().add(nuevoItem);
        }
        carrito.recalcularTotal();
    }

    @Override
    public void actualizarCantidad(HttpSession session, Long idProducto, int cantidad) {
        if (cantidad <= 0) {
            eliminarProducto(session, idProducto);
            return;
        }

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        if (producto.getStock() < cantidad) {
            throw new IllegalStateException("Stock insuficiente para la cantidad solicitada.");
        }

        CarritoDTO carrito = getCarrito(session);
        carrito.getItems().stream()
                .filter(item -> item.getIdProducto().equals(idProducto))
                .findFirst()
                .ifPresent(item -> item.setCantidad(cantidad));

        carrito.recalcularTotal();
    }

    @Override
    public void eliminarProducto(HttpSession session, Long idProducto) {
        CarritoDTO carrito = getCarrito(session);
        carrito.getItems().removeIf(item -> item.getIdProducto().equals(idProducto));
        carrito.recalcularTotal();
    }

    @Override
    public void limpiarCarrito(HttpSession session) {
        session.removeAttribute(CARRITO_SESSION_KEY);
    }
}
