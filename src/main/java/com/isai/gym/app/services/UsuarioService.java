package com.isai.gym.app.services;

import com.isai.gym.app.dtos.RegistroUsuarioDTO;
import com.isai.gym.app.entities.Usuario;

public interface UsuarioService {
    Usuario registrarUsuario(RegistroUsuarioDTO usuarioDTO);
}
