package com.isai.gym.app.services;

import com.isai.gym.app.dtos.RegistroUsuarioDTO;
import com.isai.gym.app.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UsuarioService {
    Usuario registrarUsuario(RegistroUsuarioDTO usuarioDTO);

    Page<Usuario> obtenerUsuarios(Pageable pageable);

    Page<Usuario> obtenerPorNombreOEmail(String searchTerm, Pageable pageable);

    Optional<Usuario> obtenerPorId(Long id);

    void eliminarUsuario(Long id);

    Usuario actualizarUsuario(Long id, RegistroUsuarioDTO usuarioDTO);
}
