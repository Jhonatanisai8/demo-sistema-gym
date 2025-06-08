package com.isai.gym.app.dtos;

import com.isai.gym.app.enums.TipoUsuario;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class RegistroUsuarioDTO {

    private Long id;

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    private String nombreUsuario;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un formato de email válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "La confirmación de contraseña no puede estar vacía")
    private String confirmPassword;

    @NotBlank(message = "El nombre completo no puede estar vacío")
    @Size(max = 255, message = "El nombre completo no puede exceder 255 caracteres")
    private String nombreCompleto;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @NotNull(message = "La fecha de nacimiento no puede ser nula")
    @DateTimeFormat(pattern = "yyyy-dd-MM")
    private LocalDate fechaNacimiento;

    @Size(max = 50, message = "El teléfono no puede exceder 50 caracteres")
    private String telefono;

    @Size(max = 50, message = "El género no puede exceder 50 caracteres")
    private String genero;

    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;

    @Size(max = 255, message = "El contacto de emergencia no puede exceder 255 caracteres")
    private String contactoEmergencia;

    @Size(max = 50, message = "El teléfono de emergencia no puede exceder 50 caracteres")
    private String telefonoEmergencia;

    private TipoUsuario rol;
    private String rutaImagen;

    private MultipartFile imagen;

    // Nota: Peso y Altura pueden ser opcionales en el registro inicial o dejarse para actualización de perfil
    @DecimalMin(value = "0.0", inclusive = false, message = "El peso debe ser mayor a 0")
    @Digits(integer = 5, fraction = 2, message = "El peso debe tener hasta 5 dígitos enteros y 2 decimales")
    private BigDecimal peso;

    @DecimalMin(value = "0.0", inclusive = false, message = "La altura debe ser mayor a 0")
    @Digits(integer = 5, fraction = 2, message = "La altura debe tener hasta 5 dígitos enteros y 2 decimales")
    private BigDecimal altura;
}
