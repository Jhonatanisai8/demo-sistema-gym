package com.isai.gym.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/registro/**",  // Permitir acceso a la página de registro
                                "/css/**",      // Permitir acceso a archivos CSS
                                "/js/**",       // Permitir acceso a archivos JavaScript
                                "/images/**",   // Permitir acceso a imágenes
                                "/",            // Permitir acceso a la página de inicio
                                "/login",       // Permitir acceso a la página de login
                                "/error"        // Permitir acceso a la página de error
                        ).permitAll() // Estas rutas son accesibles sin autenticación
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Solo usuarios con rol ADMIN
                        .requestMatchers("/cliente/**").hasRole("CLIENTE") // Solo usuarios con rol CLIENTE
                        .anyRequest().authenticated() // Cualquier otra solicitud requiere autenticación
                )
                .formLogin(form -> form
                        .loginPage("/login") // Especifica tu propia página de login
                        .defaultSuccessUrl("/dashboard", true) // URL a la que redirigir después de login exitoso
                        .failureUrl("/login?error=true") // URL a la que redirigir si el login falla
                        .permitAll() // Permitir que todos accedan al formulario de login
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para cerrar sesión
                        .logoutSuccessUrl("/login?logout=true") // URL a la que redirigir después de cerrar sesión
                        .permitAll()
                );
        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
