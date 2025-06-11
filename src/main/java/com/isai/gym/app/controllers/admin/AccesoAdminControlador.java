package com.isai.gym.app.controllers.admin;

import com.isai.gym.app.entities.AccesoGimnasio;
import com.isai.gym.app.entities.MembresiaCliente;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.services.impl.AccesoGimnasioServicioImpl;
import com.isai.gym.app.services.impl.MembresiaClienteServiceImpl;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/accesos")
@RequiredArgsConstructor
public class AccesoAdminControlador {


    private final UsuarioServiceImpl servicioUsuario;
    private final MembresiaClienteServiceImpl servicioMembresiaCliente;
    private final AccesoGimnasioServicioImpl servicioAccesoGimnasio;

    /**
     * Muestra el formulario para registrar un nuevo acceso de cliente (entrada).
     * Permite buscar un cliente por ID o nombre de usuario y muestra su información
     * y el estado de su membresía activa.
     *
     * @param modelo               Objeto Model para pasar datos a la vista.
     * @param identificadorCliente (Opcional) El ID o nombre de usuario del cliente a buscar.
     * @return El nombre de la vista HTML a renderizar (admin/accesos/registrar).
     */
    @GetMapping("/registrar")
    public String mostrarFormularioRegistroAcceso(
            Model modelo,
            @RequestParam(name = "identificadorCliente", required = false) String identificadorCliente) {
        if (identificadorCliente != null && !identificadorCliente.trim().isEmpty()) {
            Optional<Usuario> usuarioOpt = servicioUsuario.buscarUsuarioPorIdentificador(identificadorCliente.trim());
            if (usuarioOpt.isPresent()) {
                Usuario usuarioEncontrado = usuarioOpt.get();
                modelo.addAttribute("usuarioEncontrado", usuarioEncontrado);
                MembresiaCliente membresiaActiva = servicioMembresiaCliente.obtenerMembresiaActivaPorUsuario(usuarioEncontrado.getId());
                modelo.addAttribute("membresiaActivaCliente", membresiaActiva);
                if (membresiaActiva == null || !membresiaActiva.getActiva() || membresiaActiva.getFechaFin().isBefore(java.time.LocalDate.now())) {
                    modelo.addAttribute("mensajeAdvertencia", "¡Atención! Este cliente no tiene una membresía activa o válida.");
                } else {
                    if (servicioAccesoGimnasio.obtenerAccesoActivoPorUsuario(usuarioEncontrado.getId()).isPresent()) {
                        modelo.addAttribute("mensajeAdvertencia", "Advertencia: El cliente " + usuarioEncontrado.getNombreCompleto() + " ya tiene un acceso activo sin salida registrada. Considere registrar su salida primero.");
                    }
                }
            } else {
                modelo.addAttribute("mensajeError", "No se encontró ningún cliente con ese identificador.");
            }
            modelo.addAttribute("identificadorCliente", identificadorCliente);
        }
        if (modelo.containsAttribute("mensajeExito")) {
            modelo.addAttribute("mensajeExito", modelo.asMap().get("mensajeExito"));
        }
        if (modelo.containsAttribute("mensajeError")) {
            modelo.addAttribute("mensajeError", modelo.asMap().get("mensajeError"));
        }
        if (modelo.containsAttribute("mensajeAdvertencia")) {
            modelo.addAttribute("mensajeAdvertencia", modelo.asMap().get("mensajeAdvertencia"));
        }

        return "admin/accesos/registrar";
    }

    /**
     * Procesa el registro de la entrada de un cliente al gimnasio.
     *
     * @param usuarioId            El ID del usuario para el que se registra la entrada.
     * @param atributosRedireccion Para añadir mensajes flash a la siguiente redirección.
     * @return Redirección a la misma página del formulario de registro o a una de error.
     */
    @PostMapping("/registrar-entrada")
    public String registrarEntradaCliente(@RequestParam("usuarioId") Long usuarioId,
                                          RedirectAttributes atributosRedireccion) {
        try {

            Usuario usuario = servicioUsuario.obtenerPorId(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));

            servicioAccesoGimnasio.registrarEntrada(usuario);
            atributosRedireccion.addFlashAttribute("mensajeExito", "Entrada de '" + usuario.getNombreCompleto() + "' registrada exitosamente.");
            return "redirect:/admin/accesos/registrar";
        } catch (IllegalArgumentException e) {
            atributosRedireccion.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/admin/accesos/registrar?identificadorCliente=" + usuarioId;
        } catch (Exception e) {
            atributosRedireccion.addFlashAttribute("mensajeError", "Ocurrió un error al registrar la entrada: " + e.getMessage());
            return "redirect:/admin/accesos/registrar";
        }
    }

    /**
     * Muestra el historial completo de accesos al gimnasio, con opciones de paginación,
     * ordenamiento y filtrado por usuario o rango de fechas.
     *
     * @param modelo         Objeto Model para pasar datos a la vista.
     * @param pagina         Número de página actual (por defecto 0).
     * @param tamano         Tamaño de la página (cantidad de elementos por página, por defecto 10).
     * @param ordenarPor     Campo por el cual ordenar (por defecto "fechaHoraEntrada").
     * @param direccionOrden Dirección del ordenamiento ("asc" o "desc", por defecto "desc").
     * @param filtroUsuario  (Opcional) ID o nombre de usuario para filtrar los accesos.
     * @param fechaDesde     (Opcional) Fecha de inicio del rango para filtrar por fecha de entrada.
     * @param fechaHasta     (Opcional) Fecha de fin del rango para filtrar por fecha de entrada.
     * @return El nombre de la vista HTML a renderizar (admin/accesos/list).
     */
    @GetMapping
    public String listarTodosLosAccesos(
            Model modelo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "fechaHoraEntrada") String ordenarPor,
            @RequestParam(defaultValue = "desc") String direccionOrden,
            @RequestParam(name = "filtroUsuario", required = false) String filtroUsuario,
            @RequestParam(name = "fechaDesde", required = false) LocalDate fechaDesde,
            @RequestParam(name = "fechaHasta", required = false) LocalDate fechaHasta) {

        try {

            Sort.Direction direccion = direccionOrden.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

            Pageable paginacion = PageRequest.of(pagina, tamano, Sort.by(direccion, ordenarPor));


            Page<AccesoGimnasio> accesosPagina = servicioAccesoGimnasio.obtenerTodosLosAccesos(
                    filtroUsuario, fechaDesde, fechaHasta, paginacion);


            modelo.addAttribute("accesosPagina", accesosPagina);
            modelo.addAttribute("paginaActual", pagina);
            modelo.addAttribute("tamanoPagina", tamano);
            modelo.addAttribute("ordenarPor", ordenarPor);
            modelo.addAttribute("direccionOrden", direccionOrden);
            modelo.addAttribute("filtroUsuario", filtroUsuario);
            modelo.addAttribute("fechaDesde", fechaDesde);
            modelo.addAttribute("fechaHasta", fechaHasta);


            if (modelo.containsAttribute("mensajeExito")) {
                modelo.addAttribute("mensajeExito", modelo.asMap().get("mensajeExito"));
            }
            if (modelo.containsAttribute("mensajeError")) {
                modelo.addAttribute("mensajeError", modelo.asMap().get("mensajeError"));
            }


            return "admin/accesos/lista";
        } catch (Exception e) {

            modelo.addAttribute("mensajeError", "Ocurrió un error al cargar el historial de accesos: " + e.getMessage());
            return "admin/accesos/lista";
        }
    }

    /**
     * Procesa la acción de registrar la salida de un acceso.
     *
     * @param idAccesoGimnasio     El ID del registro de acceso a finalizar.
     * @param atributosRedireccion Para añadir mensajes flash a la siguiente redirección.
     * @return Redirección al listado de accesos del administrador.
     */
    @PostMapping("/registrar-salida")
    public String registrarSalidaAcceso(@RequestParam("idAccesoGimnasio") Long idAccesoGimnasio,
                                        RedirectAttributes atributosRedireccion) {
        try {

            servicioAccesoGimnasio.registrarSalida(idAccesoGimnasio);

            atributosRedireccion.addFlashAttribute("mensajeExito", "Salida registrada exitosamente para el acceso ID: " + idAccesoGimnasio + ".");
        } catch (IllegalArgumentException e) {

            atributosRedireccion.addFlashAttribute("mensajeError", e.getMessage());
        } catch (Exception e) {

            atributosRedireccion.addFlashAttribute("mensajeError", "Ocurrió un error al registrar la salida: " + e.getMessage());
        }

        return "redirect:/admin/accesos";
    }
}