package com.isai.gym.app.config;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

//clase que nos sirve para redirigir a una determinada pagina segun el rol
@Component
public class AuthSuccessHandler
        implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String redirectUrl = "/";

        for (GrantedAuthority authority : authorities) {

            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/dashboard";
                break;
            } else if (authority.getAuthority().equals("ROLE_CLIENTE")) {
                redirectUrl = "/cliente/dashboard";
                break;
            }
        }
        response.sendRedirect(redirectUrl);
    }
}
