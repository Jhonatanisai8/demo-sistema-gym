package com.isai.gym.app.services.impl;

import com.isai.gym.app.dtos.RegistroUsuarioDTO;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.enums.TipoUsuario;
import com.isai.gym.app.repository.UsuarioRepository;
import com.isai.gym.app.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl
        implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario registrarUsuario(RegistroUsuarioDTO usuarioDTO) {
        if (usuarioRepository.findByNombreUsuario(usuarioDTO.getNombreUsuario()).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }
        if (usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombreUsuario(usuarioDTO.getNombreUsuario());
        nuevoUsuario.setEmail(usuarioDTO.getEmail());
        nuevoUsuario.setContrasena(passwordEncoder.encode(usuarioDTO.getPassword())); // ¡Importante: Hashear la contraseña!
        nuevoUsuario.setRol(TipoUsuario.CLIENTE); // Por defecto, los registros son para clientes
        nuevoUsuario.setNombreCompleto(usuarioDTO.getNombreCompleto());
        nuevoUsuario.setFechaNacimiento(usuarioDTO.getFechaNacimiento());
        nuevoUsuario.setTelefono(usuarioDTO.getTelefono());
        nuevoUsuario.setGenero(usuarioDTO.getGenero());
        nuevoUsuario.setDireccion(usuarioDTO.getDireccion());
        nuevoUsuario.setContactoEmergencia(usuarioDTO.getContactoEmergencia());
        nuevoUsuario.setTelefonoEmergencia(usuarioDTO.getTelefonoEmergencia());
        nuevoUsuario.setPeso(usuarioDTO.getPeso());
        nuevoUsuario.setAltura(usuarioDTO.getAltura());
        // ruta de imagen se manejará aparte o se inicializará vacía
        nuevoUsuario.setRutaImagen(null); // O un valor por defecto

        // Los @PrePersist en la entidad Usuario manejarán fechaRegistro y activo
        return usuarioRepository.save(nuevoUsuario);
    }
}
