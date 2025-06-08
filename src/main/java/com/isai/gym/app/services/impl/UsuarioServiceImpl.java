package com.isai.gym.app.services.impl;

import com.isai.gym.app.dtos.RegistroUsuarioDTO;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.enums.TipoUsuario;
import com.isai.gym.app.repository.UsuarioRepository;
import com.isai.gym.app.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioServiceImpl
        implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AlmacenArchivoImpl almacenArchivo;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, AlmacenArchivoImpl almacenArchivo) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.almacenArchivo = almacenArchivo;
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
        //Para manejar las rutas
        if (usuarioDTO.getImagen() != null && !usuarioDTO.getImagen().isEmpty()) {
            String rutaImagen = almacenArchivo.almacenarArchivo(usuarioDTO.getImagen());
            nuevoUsuario.setRutaImagen(rutaImagen);
        }
        // Los @PrePersist en la entidad Usuario manejarán fechaRegistro y activo
        return usuarioRepository.save(nuevoUsuario);
    }

    @Override
    public Page<Usuario> obtenerUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    @Override
    public Page<Usuario> obtenerPorNombreOEmail(String searchTerm, Pageable pageable) {
        return usuarioRepository.findByNombreCompletoContainingIgnoreCaseOrEmailContainingIgnoreCase(searchTerm, searchTerm, pageable);
    }

    @Override
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public void eliminarUsuario(Long id) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            if (usuario.getRutaImagen() != null && !usuario.getRutaImagen().isEmpty()) {
                almacenArchivo.eliminarArchivo(usuario.getRutaImagen());
            }
        });
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario actualizarUsuario(Long id, RegistroUsuarioDTO usuarioDTO) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }
        Usuario usuarioExistente = usuarioOptional.get();

        // Validar unicidad si el nombre de usuario cambia
        if (!usuarioDTO.getNombreUsuario().equals(usuarioExistente.getNombreUsuario())) {
            if (usuarioRepository.findByNombreUsuario(usuarioDTO.getNombreUsuario()).isPresent()) {
                throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
            }
        }
        // Validar unicidad si el email cambia
        if (!usuarioDTO.getEmail().equals(usuarioExistente.getEmail())) {
            if (usuarioRepository.findByEmail(usuarioDTO.getEmail()).isPresent()) {
                throw new IllegalArgumentException("El email ya está registrado.");
            }
        }

        // Actualizar campos
        usuarioExistente.setNombreUsuario(usuarioDTO.getNombreUsuario());
        usuarioExistente.setEmail(usuarioDTO.getEmail());

        // Actualizar contraseña solo si se proporciona una nueva
        if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().isEmpty()) {
            usuarioExistente.setContrasena(passwordEncoder.encode(usuarioDTO.getPassword()));
        }

        // El rol es asignado por el admin
        if (usuarioDTO.getRol() != null) {
            usuarioExistente.setRol(usuarioDTO.getRol());
        }

        usuarioExistente.setNombreCompleto(usuarioDTO.getNombreCompleto());
        usuarioExistente.setFechaNacimiento(usuarioDTO.getFechaNacimiento());
        usuarioExistente.setTelefono(usuarioDTO.getTelefono());
        usuarioExistente.setGenero(usuarioDTO.getGenero());
        usuarioExistente.setDireccion(usuarioDTO.getDireccion());
        usuarioExistente.setContactoEmergencia(usuarioDTO.getContactoEmergencia());
        usuarioExistente.setTelefonoEmergencia(usuarioDTO.getTelefonoEmergencia());
        usuarioExistente.setPeso(usuarioDTO.getPeso());
        usuarioExistente.setAltura(usuarioDTO.getAltura());

        if (usuarioDTO.getImagen() != null && !usuarioDTO.getImagen().isEmpty()) {

            if (usuarioExistente.getRutaImagen() != null && !usuarioExistente.getRutaImagen().isEmpty()) {
                almacenArchivo.eliminarArchivo(usuarioExistente.getRutaImagen());
            }
            String nuevaRutaImagen = almacenArchivo.almacenarArchivo(usuarioDTO.getImagen());
            usuarioExistente.setRutaImagen(nuevaRutaImagen);
        } else if (usuarioDTO.getImagen() != null && usuarioDTO.getImagen().isEmpty() && usuarioExistente.getRutaImagen() != null && !usuarioExistente.getRutaImagen().isEmpty()) {
            almacenArchivo.eliminarArchivo(usuarioExistente.getRutaImagen());
            usuarioExistente.setRutaImagen(usuarioExistente.getRutaImagen());
        }
        return usuarioRepository.save(usuarioExistente);
    }
}
