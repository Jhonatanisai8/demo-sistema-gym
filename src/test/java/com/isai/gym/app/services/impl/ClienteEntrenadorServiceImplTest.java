package com.isai.gym.app.services.impl;

import com.isai.gym.app.dtos.ClienteEntrenadorDTO;
import com.isai.gym.app.entities.ClienteEntrenador;
import com.isai.gym.app.entities.Entrenador;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.repository.ClienteEntrenadorRepository;
import com.isai.gym.app.repository.EntrenadorRepository;
import com.isai.gym.app.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClienteEntrenadorServiceImplTest {

    @Mock
    private ClienteEntrenadorRepository clienteEntrenadorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EntrenadorRepository entrenadorRepository;

    @InjectMocks
    private ClienteEntrenadorServiceImpl clienteEntrenadorService;

    private ClienteEntrenadorDTO clienteEntrenadorDTO;
    private Usuario usuario;
    private Entrenador entrenador;
    private ClienteEntrenador clienteEntrenador;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);

        entrenador = new Entrenador();
        entrenador.setId(1L);

        clienteEntrenadorDTO = new ClienteEntrenadorDTO();
        clienteEntrenadorDTO.setUsuarioId(1L);
        clienteEntrenadorDTO.setEntrenadorId(1L);
        clienteEntrenadorDTO.setFechaInicio(LocalDate.now());
        clienteEntrenadorDTO.setFechaFin(LocalDate.now().plusMonths(1));

        clienteEntrenador = new ClienteEntrenador();
        clienteEntrenador.setId(1L);
        clienteEntrenador.setUsuario(usuario);
        clienteEntrenador.setEntrenador(entrenador);
    }

    @Test
    void testGuardar_Exitoso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(entrenadorRepository.findById(1L)).thenReturn(Optional.of(entrenador));
        when(clienteEntrenadorRepository.findConflictingAssignments(any(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(clienteEntrenadorRepository.save(any(ClienteEntrenador.class))).thenReturn(clienteEntrenador);

        ClienteEntrenador result = clienteEntrenadorService.guardar(clienteEntrenadorDTO);

        assertNotNull(result);
        assertEquals(usuario.getId(), result.getUsuario().getId());
        assertEquals(entrenador.getId(), result.getEntrenador().getId());
    }

    @Test
    void testGuardar_ConflictoDeFechas() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(entrenadorRepository.findById(1L)).thenReturn(Optional.of(entrenador));
        when(clienteEntrenadorRepository.findConflictingAssignments(any(), any(), any(), any())).thenReturn(List.of(clienteEntrenador));

        assertThrows(IllegalArgumentException.class, () -> {
            clienteEntrenadorService.guardar(clienteEntrenadorDTO);
        });
    }

    @Test
    void testObtenerPorID_Exitoso() {
        when(clienteEntrenadorRepository.findById(1L)).thenReturn(Optional.of(clienteEntrenador));

        Optional<ClienteEntrenador> result = clienteEntrenadorService.obtenerPorID(1L);

        assertTrue(result.isPresent());
        assertEquals(clienteEntrenador.getId(), result.get().getId());
    }
}
