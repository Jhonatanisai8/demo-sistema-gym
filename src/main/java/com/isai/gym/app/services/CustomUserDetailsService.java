package com.isai.gym.app.services;

import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //buscamos el usuario por su nombre de usuario
        Usuario usuario =
                usuarioRepository.findByNombreUsuario(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return usuario;
    }
}
