package com.isai.gym.app.repository;

import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.enums.TipoUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNombreUsuario(@NotBlank(message = "El nombre de usuario no puede estar vacío") @Size(min = 4, max = 255, message = "El nombre de usuario debe tener entre 4 y 255 caracteres") String nombreUsuario);

    Optional<Usuario> findByEmail(String email);

    Page<Usuario> findByNombreCompletoContainingIgnoreCaseOrEmailContainingIgnoreCase(String nombreCompleto, String email, Pageable pageable);

    @Query("SELECT count(u) FROM Usuario u WHERE u.rol = :rol AND u.activo = :activo")
    long countByRolAndActivo(@Param("rol") TipoUsuario rol, @Param("activo") boolean activo);

}
