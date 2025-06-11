package com.isai.gym.app.services.impl;

import com.isai.gym.app.entities.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;

import org.springframework.ai.chat.messages.Message;         // Importación correcta
import org.springframework.ai.chat.messages.SystemMessage;   // Importación correcta
import org.springframework.ai.chat.messages.UserMessage;     // Importación correcta

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AsesorIAImpl {

    private final OllamaChatModel chatModel;
    private final EquipoServiceImpl equipoServicio;
    private final UsuarioServiceImpl usuarioService;

    /**
     * Procesa una pregunta del usuario, generando una sugerencia de ejercicios personalizada
     * que considera el inventario de equipo del gimnasio y los datos de peso/altura del usuario.
     *
     * @param preguntaUsuario La pregunta del usuario (ej. "¿Qué ejercicios puedo hacer para piernas?").
     * @param usuarioId       El ID del usuario autenticado que realiza la pregunta.
     * @return La sugerencia de ejercicios generada por la IA.
     * @throws RuntimeException Si ocurre un error irrecuperable durante el proceso.
     */
    public String obtenerSugerenciaEjercicios(String preguntaUsuario, Long usuarioId) {
        try {
            String inventarioDisponible = equipoServicio.obtenerDescripcionInventarioDisponible();

            Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(usuarioId);
            String datosUsuario = "";
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                String alturaStr = (usuario.getAltura() != null) ? usuario.getAltura().stripTrailingZeros().toPlainString() : "no especificada";
                String pesoStr = (usuario.getPeso() != null) ? usuario.getPeso().stripTrailingZeros().toPlainString() : "no especificado";

                datosUsuario = String.format(
                        "El usuario que pregunta tiene %s cm de altura y pesa %s kg. ",
                        alturaStr,
                        pesoStr
                );
            } else {
                datosUsuario = "No se pudieron obtener los datos de altura y peso del usuario. ";
            }

            String systemMessageContent = """
                    Eres un asesor de fitness experto y amable para un gimnasio. Tu objetivo es sugerir
                    rutinas y ejercicios personalizados a los usuarios.
                    
                    **Consideraciones Importantes para tus Sugerencias:**
                    - **Datos del Usuario:** %s
                    - **Inventario de Equipo Disponible:** %s
                    
                    **Pautas para tus Respuestas:**
                    - Sé claro, conciso, motivador y fácil de entender.
                    - SIEMPRE basa tus recomendaciones en el equipo **disponible** en el gimnasio y los datos del usuario (altura y peso).
                    - Si el usuario pregunta por un ejercicio que requiere equipo que NO está en el inventario disponible,
                      sugiérele alternativas o indícale amablemente qué SÍ se puede hacer con el equipo existente.
                    - Si no hay equipo disponible (según la lista), enfócate en ejercicios de peso corporal.
                    - Proporciona ejemplos concretos de ejercicios o rutinas.
                    - Evita generar código (como SQL) o información que no sea directamente una sugerencia de ejercicio.
                    - Mantén un tono profesional pero cercano y alentador.
                    """.formatted(datosUsuario, inventarioDisponible);

            List<Message> messages = new ArrayList<>();
            messages.add(new SystemMessage(systemMessageContent));
            messages.add(new UserMessage(preguntaUsuario));

            Prompt prompt = new Prompt(messages);

            ChatResponse chatResponse = chatModel.call(prompt);

            String respuestaIA = chatResponse.getResults().get(0).getOutput().getText();

            return respuestaIA;

        } catch (Exception e) {
            System.err.println("**************************************************************");
            System.err.println("ERROR EN AsesorIAImpl.obtenerSugerenciaEjercicios:");
            e.printStackTrace();
            System.err.println("**************************************************************");
            throw new RuntimeException("Error en el servicio de IA: " + e.getMessage(), e);
        }
    }
}