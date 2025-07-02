package com.isai.gym.app.controllers.cliente;

import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.services.impl.AsesorIAImpl;
import com.isai.gym.app.services.impl.UsuarioServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/cliente/ia")
@PreAuthorize("hasRole('CLIENTE')")
@RequiredArgsConstructor
public class AsesorIAController {


    private final AsesorIAImpl asesorIAServicio;
    private final UsuarioServiceImpl usuarioService;

    /**
     * Muestra la interfaz de chat con el asesor de IA para el cliente.
     * Este método se ejecuta cuando el cliente navega a /cliente/ia/sugerencias.
     *
     * @param modelo Objeto Model para pasar atributos a la vista (ej. mensajes de error).
     * @return El nombre lógico de la vista HTML (ubicada en src/main/resources/templates/client/ia/sugerencias.html).
     */
    @GetMapping("/sugerencias")
    public String mostrarSugerenciasIA(Model modelo) {

        if (modelo.containsAttribute("mensajeError")) {
            modelo.addAttribute("mensajeError", modelo.asMap().get("mensajeError"));
        }
        return "cliente/ia/sugerencias";
    }

    /**
     * Procesa la pregunta del usuario enviada a través de AJAX desde el frontend.
     * Llama al servicio de IA para obtener una sugerencia personalizada y devuelve la respuesta en JSON.
     *
     * @param pregunta  La pregunta de texto que el usuario ha escrito.
     * @param principal El objeto Principal proporcionado por Spring Security, que contiene el nombre del usuario autenticado.
     * @return Un mapa (que Spring automáticamente serializa a JSON) conteniendo la respuesta de la IA.
     */
    @PostMapping("/sugerencias")
    @ResponseBody
    public Map<String, String> obtenerRespuestaIA(@RequestParam("pregunta") String pregunta,
                                                  Principal principal) {
        Map<String, String> respuestaJson = new HashMap<>();


        if (principal == null) {

            respuestaJson.put("respuestaIA", "Error: No se pudo identificar al usuario. Por favor, asegúrate de estar logueado.");
            return respuestaJson;
        }

        try {


            Optional<Usuario> usuarioAutenticadoOpt = usuarioService.buscarPorNombreUsuario(principal.getName());


            if (usuarioAutenticadoOpt.isEmpty()) {
                throw new IllegalArgumentException("Usuario autenticado no encontrado en la base de datos. Por favor, contacta a soporte.");
            }
            Usuario usuarioAutenticado = usuarioAutenticadoOpt.get();


            String sugerencia = asesorIAServicio.obtenerSugerenciaEjercicios(pregunta, usuarioAutenticado.getId());
            respuestaJson.put("respuestaIA", sugerencia);

        } catch (IllegalArgumentException e) {

            System.err.println("Error de validación o al obtener datos del usuario: " + e.getMessage());
            respuestaJson.put("respuestaIA", "Lo siento, hubo un problema al obtener tus datos: " + e.getMessage());
        } catch (Exception e) {

            System.err.println("Error general al obtener sugerencia de la IA: " + e.getMessage());
            respuestaJson.put("respuestaIA", "Lo siento, ocurrió un error inesperado al procesar tu solicitud. Por favor, intenta de nuevo más tarde.");

        }
        return respuestaJson;
    }
}
