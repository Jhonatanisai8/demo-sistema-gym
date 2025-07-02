package com.isai.gym.app.services;

import com.isai.gym.app.dtos.CarritoDTO;
import jakarta.servlet.http.HttpSession;

public interface CarritoService {
    CarritoDTO getCarrito(HttpSession session);

    void agregarProducto(HttpSession session, Long idProducto, int cantidad);

    void actualizarCantidad(HttpSession session, Long idProducto, int cantidad);

    void eliminarProducto(HttpSession session, Long idProducto);

    void limpiarCarrito(HttpSession session);
}
