package com.isai.gym.app.controllers.cliente;

import com.isai.gym.app.entities.AccesoGimnasio;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.services.impl.AccesoGimnasioServicioImpl;
import com.isai.gym.app.services.impl.UsuarioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/cliente/accesos")
@PreAuthorize("hasRole('CLIENTE')")
@RequiredArgsConstructor
public class AccesoClienteController {

    private final AccesoGimnasioServicioImpl servicioAccesoGimnasio;

    private final UsuarioServiceImpl servicioUsuario;

    /**
     * Muestra el historial de accesos al gimnasio para el cliente autenticado, con paginación y ordenamiento.
     *
     * @param modelo         Objeto Model para pasar datos a la vista.
     * @param principal      Objeto Principal para obtener el usuario autenticado.
     * @param pagina         Número de página solicitado (por defecto 0).
     * @param tamano         Tamaño de página (por defecto 10).
     * @param ordenarPor     Campo por el cual ordenar (por defecto "fechaHoraEntrada").
     * @param direccionOrden Dirección del ordenamiento ("asc" o "desc", por defecto "desc").
     * @return Nombre de la vista HTML.
     */

    @GetMapping

    public String listarHistorialAccesos(
            Model modelo,
            Principal principal,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "fechaHoraEntrada") String ordenarPor,
            @RequestParam(defaultValue = "desc") String direccionOrden) {

        try {
            Usuario clienteAutenticado = servicioUsuario.buscarPorNombreUsuarioOEmail(principal.getName());
            Long idUsuario = clienteAutenticado.getId();

            Sort.Direction direccion = direccionOrden.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable paginacion = PageRequest.of(pagina, tamano, Sort.by(direccion, ordenarPor));

            Page<AccesoGimnasio> accesosPagina = servicioAccesoGimnasio.obtenerAccesosPorUsuario(idUsuario, paginacion);

            modelo.addAttribute("accesosPagina", accesosPagina);
            modelo.addAttribute("paginaActual", pagina);
            modelo.addAttribute("tamanoPagina", tamano);
            modelo.addAttribute("ordenarPor", ordenarPor);
            modelo.addAttribute("direccionOrden", direccionOrden);

            if (modelo.containsAttribute("mensajeExito")) {
                modelo.addAttribute("mensajeExito", modelo.asMap().get("mensajeExito"));
            }
            if (modelo.containsAttribute("mensajeError")) {
                modelo.addAttribute("mensajeError", modelo.asMap().get("mensajeError"));
            }

            return "cliente/accesos/lista-accesos";
        } catch (IllegalArgumentException e) {
            modelo.addAttribute("mensajeError", e.getMessage());
            return "redirect:/cliente/dashboard";
        } catch (Exception e) {
            modelo.addAttribute("mensajeError", "Ocurrió un error al cargar tu historial de accesos: " + e.getMessage());
            return "cliente/accesos/lista-accesos";
        }
    }


}
