package com.isai.gym.app.controllers.admin;

import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.enums.TipoUsuario;
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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequestMapping(path = "/admin/usuarios")
@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUsuarioController {

    private final UsuarioServiceImpl usuarioService;

    @GetMapping(path = "/lista")
    public String listarUsuarios(@RequestParam(name = "page", defaultValue = "0") int page, // Página actual (0-indexed)
                                 @RequestParam(name = "size", defaultValue = "10") int size, // Tamaño de elementos por página
                                 @RequestParam(name = "search", required = false) String searchTerm, // Término de búsqueda opcional
                                 Model model) {
        Pageable pageable = PageRequest.of(Math.max(0, page), size);
        Page<Usuario> usuarioPage;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            usuarioPage = usuarioService.obtenerPorNombreOEmail(searchTerm, pageable);
        } else {
            usuarioPage = usuarioService.obtenerUsuarios(pageable);
        }
        model.addAttribute("usuariosPage", usuarioPage);
        //generamos la lista con paginacion
        int totalPaginas = usuarioPage.getTotalPages();
        if (totalPaginas > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPaginas)
                    .boxed().
                    collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("tittulo", "Listado de Usuarios."); // Nota: "tittulo" tiene un typo, debería ser "titulo"
        model.addAttribute("searchTerm", searchTerm);
        return "admin/usuarios/lista";
    }

}
