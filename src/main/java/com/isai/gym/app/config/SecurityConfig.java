package com.isai.gym.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler; // Asegúrate de tener esta importación si usas AuthSuccessHandler

@Configuration // Indica que esta clase contiene definiciones de beans de configuración
@EnableWebSecurity // Habilita las características de seguridad web de Spring Security
public class SecurityConfig {

    // Inyección de dependencia para el manejador de éxito de autenticación personalizado
    private final AuthenticationSuccessHandler authSuccessHandler; // Cambiado a la interfaz para mayor flexibilidad

    /**
     * Constructor para inyección de dependencias.
     *
     * @param authSuccessHandler El manejador de éxito de autenticación personalizado.
     */
    public SecurityConfig(AuthenticationSuccessHandler authSuccessHandler) {
        this.authSuccessHandler = authSuccessHandler;
    }

    /**
     * Define la cadena de filtros de seguridad HTTP, configurando las reglas de autorización,
     * el formulario de login y el proceso de logout.
     *
     * @param http El objeto HttpSecurity para configurar la seguridad.
     * @return Un SecurityFilterChain configurado.
     * @throws Exception Si ocurre un error durante la configuración de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configuración de las reglas de autorización para las peticiones HTTP
                .authorizeHttpRequests(authorize -> authorize
                        // Permite acceso sin autenticación a ciertas rutas y recursos estáticos
                        .requestMatchers(
                                "/registro/**", // Ruta para el registro de usuarios
                                "/css/**",      // Archivos CSS
                                "/js/**",       // Archivos JavaScript
                                "/imgs/**",   // Imágenes
                                "/uploads/**",  // Archivos subidos (ej. imágenes de perfil, si las tienes)
                                "/",            // Página de inicio
                                "/login",       // Página de login
                                "/error"        // Página de error genérica
                        ).permitAll() // Permite el acceso a todos los usuarios (autenticados o no)

                        // Rutas de administrador: Requieren el rol 'ADMIN'
                        // Cualquier URL que comience con /admin/
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Rutas de cliente: Requieren el rol 'CLIENTE'
                        // Esto incluye /cliente/ia/sugerencias. Spring Security buscará ROLE_CLIENTE.
                        .requestMatchers("/cliente/**").hasRole("CLIENTE")
                        .requestMatchers("/index").permitAll()
                        // Cualquier otra petición no especificada anteriormente requiere autenticación
                        .anyRequest().authenticated()
                )
                // Configuración del formulario de login
                .formLogin(form -> form
                        .loginPage("/login") // Especifica la URL de la página de login
                        // Usa el manejador de éxito de autenticación personalizado
                        .successHandler(authSuccessHandler)
                        .failureUrl("/login?error=true") // URL a la que redirigir en caso de login fallido
                        .permitAll() // Permite que todos accedan a la página de login (esencial)
                )
                // Configuración del proceso de logout
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para procesar el logout
                        .logoutSuccessUrl("/login?logout=true") // URL a la que redirigir después del logout exitoso
                        .permitAll() // Permite que todos accedan a la URL de logout
                )
                .csrf(csrf -> csrf.disable()); // <-- ¡AÑADE ESTA LÍNEA PARA PRUEBAS!


        return http.build(); // Construye y devuelve la cadena de filtros de seguridad
    }

    /**
     * Define un bean para el codificador de contraseñas.
     * Es esencial para codificar y verificar las contraseñas de los usuarios.
     *
     * @return Una instancia de BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
