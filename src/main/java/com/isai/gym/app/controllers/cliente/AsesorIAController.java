package com.isai.gym.app.controllers.cliente;

import com.isai.gym.app.entities.Usuario; // Importa la entidad Usuario
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
import org.springframework.web.bind.annotation.ResponseBody; // Para retornar JSON directamente
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal; // Para obtener información del usuario autenticado
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller // Marca esta clase como un controlador de Spring MVC
@RequestMapping("/cliente/ia") // Mapea todas las URLs de este controlador bajo /cliente/ia
@PreAuthorize("hasRole('CLIENTE')")
@RequiredArgsConstructor // Genera un constructor con todos los campos 'final' para inyección de dependencias
public class AsesorIAController {


    private final AsesorIAImpl asesorIAServicio; // Servicio que interactúa con la IA
    private final UsuarioServiceImpl usuarioService;     // Servicio para obtener los datos del usuario autenticado

    /**
     * Muestra la interfaz de chat con el asesor de IA para el cliente.
     * Este método se ejecuta cuando el cliente navega a /cliente/ia/sugerencias.
     *
     * @param modelo Objeto Model para pasar atributos a la vista (ej. mensajes de error).
     * @return El nombre lógico de la vista HTML (ubicada en src/main/resources/templates/client/ia/sugerencias.html).
     */
    @GetMapping("/sugerencias")
    public String mostrarSugerenciasIA(Model modelo) {
        // Recupera mensajes de error que pudieron haber sido añadidos por una redirección anterior (ej. si algo falló al loguear).
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
    @ResponseBody // Indica que el valor de retorno de este método debe ser directamente el cuerpo de la respuesta HTTP.
    public Map<String, String> obtenerRespuestaIA(@RequestParam("pregunta") String pregunta,
                                                  Principal principal) {
        Map<String, String> respuestaJson = new HashMap<>(); // Mapa para construir la respuesta JSON.

        // Validar si el usuario está autenticado.
        if (principal == null) {
            // Aunque Spring Security debería manejar esto, es una defensa en profundidad.
            respuestaJson.put("respuestaIA", "Error: No se pudo identificar al usuario. Por favor, asegúrate de estar logueado.");
            return respuestaJson;
        }

        try {
            // 1. Obtener la entidad Usuario completa a partir del nombre de usuario autenticado.
            // Asumimos que `principal.getName()` devuelve el 'nombre_usuario' único del cliente.
            Optional<Usuario> usuarioAutenticadoOpt = usuarioService.buscarPorNombreUsuario(principal.getName());

            // Si el usuario no se encuentra en la base de datos (a pesar de estar autenticado, lo cual sería un error crítico).
            if (usuarioAutenticadoOpt.isEmpty()) {
                throw new IllegalArgumentException("Usuario autenticado no encontrado en la base de datos. Por favor, contacta a soporte.");
            }
            Usuario usuarioAutenticado = usuarioAutenticadoOpt.get();

            // 2. Llamar al servicio de IA, pasando la pregunta del usuario y el ID del usuario autenticado.
            // El servicio usará este ID para obtener el peso y la altura del usuario.
            String sugerencia = asesorIAServicio.obtenerSugerenciaEjercicios(pregunta, usuarioAutenticado.getId());
            respuestaJson.put("respuestaIA", sugerencia); // La clave "respuestaIA" debe coincidir con el JavaScript de la vista.

        } catch (IllegalArgumentException e) {
            // Captura errores específicos de validación o lógica de negocio (ej. usuario no encontrado).
            System.err.println("Error de validación o al obtener datos del usuario: " + e.getMessage());
            respuestaJson.put("respuestaIA", "Lo siento, hubo un problema al obtener tus datos: " + e.getMessage());
        } catch (Exception e) {
            // Captura cualquier otra excepción inesperada durante la interacción con la IA o la DB.
            System.err.println("Error general al obtener sugerencia de la IA: " + e.getMessage());
            respuestaJson.put("respuestaIA", "Lo siento, ocurrió un error inesperado al procesar tu solicitud. Por favor, intenta de nuevo más tarde.");
            // En un entorno de producción real, usar un logger profesional (ej. SLF4J, Logback) es crucial aquí.
        }
        return respuestaJson; // Devuelve el mapa JSON como respuesta HTTP.
    }
}
