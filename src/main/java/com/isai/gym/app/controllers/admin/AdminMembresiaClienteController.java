package com.isai.gym.app.controllers.admin;

import com.isai.gym.app.dtos.MembresiaClienteDTO;
import com.isai.gym.app.entities.MembresiaCliente;
import com.isai.gym.app.enums.EstadoMembresia;
import com.isai.gym.app.services.impl.MembresiaClienteServiceImpl;
import com.isai.gym.app.services.impl.MembresiaServiceImpl;
import com.isai.gym.app.services.impl.UsuarioServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/membresias/clientes")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminMembresiaClienteController {

    private final MembresiaClienteServiceImpl membresiaClienteServiceImpl;

    private final MembresiaServiceImpl membresiaServiceImpl;

    private final UsuarioServiceImpl usuarioServiceImpl;

    //cargamos datos comunes
    private void loadFormDependencias(Model model) {
        model.addAttribute("usuarios", usuarioServiceImpl.obtenerUsuarios(PageRequest.of(0, 1000, Sort.by("nombreCompleto"))).getContent()); // Cargar todos los usuarios (ajustar paginación si hay muchos)
        model.addAttribute("tiposMembresia", membresiaServiceImpl.obtenerMembresias(PageRequest.of(0, 1000, Sort.by("nombre"))).getContent()); // Cargar todos los tipos de membresía
        model.addAttribute("estadosMembresia", EstadoMembresia.values()); // Enum para el combobox de estados
    }

    @GetMapping({"", "/"})
    public String listarMembresiasCliente(Model model,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "id,asc") String[] sort,
                                          @RequestParam(required = false) String keyword) {

        String sortBy = sort[0];
        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<MembresiaCliente> membresiasClientePage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            membresiasClientePage = membresiaClienteServiceImpl.buscar(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            membresiasClientePage = membresiaClienteServiceImpl.obtenerTodasMembresias(pageable);
        }

        model.addAttribute("membresiasCliente", membresiasClientePage);
        model.addAttribute("currentPage", membresiasClientePage.getNumber());
        model.addAttribute("totalPages", membresiasClientePage.getTotalPages());
        model.addAttribute("totalItems", membresiasClientePage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", direction.toString());
        model.addAttribute("reverseSortDirection", direction.equals(Sort.Direction.ASC) ? "desc" : "asc");

        int totalPages = membresiasClientePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(0, totalPages - 1)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "admin/membresias/clientes/lista";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("membresiaClienteDTO", new MembresiaClienteDTO());
        loadFormDependencias(model);
        return "admin/membresias/clientes/crear";
    }

    @PostMapping("/crear")
    public String crearMembresiaCliente(@Valid @ModelAttribute("membresiaClienteDTO") MembresiaClienteDTO membresiaClienteDTO,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes,
                                        Model model) {
        if (bindingResult.hasErrors()) {
            loadFormDependencias(model);
            return "admin/membresias/clientes/crear";
        }

        try {
            membresiaClienteServiceImpl.guardar(membresiaClienteDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Membresía de cliente creada exitosamente!");
            return "redirect:/admin/membresias/clientes";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            loadFormDependencias(model);
            return "admin/membresias/clientes/crear";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear la membresía de cliente: " + e.getMessage());
            return "redirect:/admin/membresias/clientes/crear";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<MembresiaCliente> membresiaClienteOptional = membresiaClienteServiceImpl.obtenerPorID(id);
        if (membresiaClienteOptional.isPresent()) {
            MembresiaCliente membresiaCliente = membresiaClienteOptional.get();
            // Mapea la entidad a un DTO para el formulario
            MembresiaClienteDTO membresiaClienteDTO = new MembresiaClienteDTO();
            membresiaClienteDTO.setId(membresiaCliente.getId());
            membresiaClienteDTO.setUsuarioId(membresiaCliente.getUsuario().getId());
            membresiaClienteDTO.setMembresiaId(membresiaCliente.getMembresia().getId());
            membresiaClienteDTO.setFechaInicio(membresiaCliente.getFechaInicio());
            membresiaClienteDTO.setFechaFin(membresiaCliente.getFechaFin());
            membresiaClienteDTO.setActiva(membresiaCliente.getActiva());
            membresiaClienteDTO.setEstado(membresiaCliente.getEstado());
            membresiaClienteDTO.setMontoPagado(membresiaCliente.getMontoPagado());
            membresiaClienteDTO.setMetodoPago(membresiaCliente.getMetodoPago());

            model.addAttribute("membresiaClienteDTO", membresiaClienteDTO);
            loadFormDependencias(model); // cargamos usuarios y tipos de membresía
            return "admin/membresias/clientes/detalle";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Membresía de cliente no encontrada.");
            return "redirect:/admin/membresias/clientes";
        }
    }

    @PostMapping("/editar/{id}")
    public String actualizarMembresiaCliente(@PathVariable Long id,
                                             @Valid @ModelAttribute("membresiaClienteDTO") MembresiaClienteDTO membresiaClienteDTO,
                                             BindingResult bindingResult,
                                             RedirectAttributes redirectAttributes,
                                             Model model) {

        if (!id.equals(membresiaClienteDTO.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error de seguridad: ID de membresía de cliente no coincide.");
            return "redirect:/admin/membresias/clientes";
        }

        if (bindingResult.hasErrors()) {
            loadFormDependencias(model);
            return "admin/membresias/clientes/detalle";
        }

        try {
            Optional<MembresiaCliente> updatedMembresia = membresiaClienteServiceImpl.actualizar(id, membresiaClienteDTO);

            if (updatedMembresia.isPresent()) {
                redirectAttributes.addFlashAttribute("successMessage", "Membresía de cliente actualizada exitosamente!");
                return "redirect:/admin/membresias/clientes";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Membresía de cliente no encontrada para actualizar.");
                return "redirect:/admin/membresias/clientes";
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            loadFormDependencias(model);
            return "admin/membresias/clientes/detalle";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar la membresía de cliente: " + e.getMessage());
            return "redirect:/admin/membresias/clientes/editar/" + id;
        }
    }

    @GetMapping("/eliminar/{id}")
    public String confirmarEliminarMembresiaCliente(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<MembresiaCliente> membresiaClienteBD = membresiaClienteServiceImpl.obtenerPorID(id);
        if (membresiaClienteBD.isPresent()) {
            model.addAttribute("membresiaClienteBD", membresiaClienteBD.get());
            return "admin/membresias/clientes/confirmar-eliminar";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Membresia-Cliente no encontrada.");
            return "redirect:/admin/membresias/clientes";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarMembresiaCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (membresiaClienteServiceImpl.eliminarPorID(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "Membresia-Cliente eliminada exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el tipo de membresía-cliente. Podría estar asociado a membresías de clientes.");
        }
        return "redirect:/admin/membresias/clientes";
    }

    @PostMapping("/toggleActiva/{id}")
    public String toggleActiva(@PathVariable Long id,
                               @RequestParam("activa") boolean activa,
                               RedirectAttributes redirectAttributes) {
        Optional<MembresiaCliente> membresiaClienteOptional = membresiaClienteServiceImpl.toggleActiva(id, activa);
        if (membresiaClienteOptional.isPresent()) {
            String mensaje = membresiaClienteOptional.get().getActiva() ? "activado" : "desactivado";
            redirectAttributes.addFlashAttribute("successMessage", "Membresía de cliente " + mensaje + " exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Membresía de cliente no encontrada.");
        }
        return "redirect:/admin/membresias/clientes";
    }
}
