package com.isai.gym.app.entities;


import com.isai.gym.app.enums.TipoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario
        implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 4, max = 255, message = "El nombre de usuario debe tener entre 4 y 255 caracteres")
    @Column(name = "nombre_usuario", unique = true, nullable = false)
    private String nombreUsuario;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(name = "contrasena", nullable = false)
    private String contrasena; // Se recomienda almacenar contraseñas hasheadas

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un formato de email válido")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotNull(message = "El rol no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private TipoUsuario rol;

    @NotBlank(message = "El nombre completo no puede estar vacío")
    @Size(max = 255, message = "El nombre completo no puede exceder 255 caracteres")
    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @Column(name = "fecha_nacimiento")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;

    @Size(max = 50, message = "El teléfono no puede exceder 50 caracteres")
    @Column(name = "telefono")
    private String telefono;

    @NotNull(message = "La fecha de registro no puede ser nula")
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @NotNull(message = "El estado activo no puede ser nulo")
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Size(max = 50, message = "El género no puede exceder 50 caracteres")
    @Column(name = "genero")
    private String genero;

    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;

    @Size(max = 255, message = "El contacto de emergencia no puede exceder 255 caracteres")
    @Column(name = "contacto_emergencia")
    private String contactoEmergencia;

    @Size(max = 50, message = "El teléfono de emergencia no puede exceder 50 caracteres")
    @Column(name = "telefono_emergencia")
    private String telefonoEmergencia;

    @DecimalMin(value = "0.0", inclusive = false, message = "El peso debe ser mayor a 0")
    @Digits(integer = 5, fraction = 2, message = "El peso debe tener hasta 5 dígitos enteros y 2 decimales")
    @Column(name = "peso")
    private BigDecimal peso;

    @DecimalMin(value = "0.0", inclusive = false, message = "La altura debe ser mayor a 0")
    @Digits(integer = 5, fraction = 2, message = "La altura debe tener hasta 5 dígitos enteros y 2 decimales")
    @Column(name = "altura")
    private BigDecimal altura;

    private String rutaImagen;

    @Transient
    private MultipartFile foto;

    // --- Relaciones ---
    // Un usuario puede tener muchas membresías de cliente
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MembresiaCliente> membresiasCliente = new ArrayList<>();

    // Un usuario puede estar asignado a muchos entrenadores (historial)
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClienteEntrenador> clientesEntrenador = new ArrayList<>();

    // Un usuario puede realizar muchas ventas (como comprador)
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Venta> comprasRealizadas = new ArrayList<>();

    // Un usuario puede ser el vendedor en muchas ventas
    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Venta> ventasComoVendedor = new ArrayList<>();

    // Un usuario puede tener muchos accesos al gimnasio
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AccesoGimnasio> accesosGimnasio = new ArrayList<>();

    // Un usuario puede realizar muchos pagos
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pago> pagos = new ArrayList<>();

    // Constructor para valores por defecto si no usas @AllArgsConstructor
    @PrePersist // Se ejecuta antes de persistir la entidad por primera vez
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getPassword() {
        return contrasena;
    }

    @Override
    public String getUsername() {
        return nombreUsuario;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}