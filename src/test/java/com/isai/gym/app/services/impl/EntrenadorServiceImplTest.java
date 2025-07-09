package com.isai.gym.app.services.impl;

import com.isai.gym.app.dtos.EntrenadorDTO;
import com.isai.gym.app.entities.Entrenador;
import com.isai.gym.app.repository.EntrenadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EntrenadorServiceImplTest {

    @Mock
    private EntrenadorRepository entrenadorRepository;

    @Mock
    private AlmacenArchivoImpl almacenServicio;

    @InjectMocks
    private EntrenadorServiceImpl entrenadorService;

    private EntrenadorDTO entrenadorDTO;
    private Entrenador entrenador;

    @BeforeEach
    void setUp() {
        entrenadorDTO = new EntrenadorDTO();
        entrenadorDTO.setNombre("Juan Perez");
        entrenadorDTO.setEmail("juan.perez@example.com");

        entrenador = new Entrenador();
        entrenador.setId(1L);
        entrenador.setNombre("Juan Perez");
        entrenador.setEmail("juan.perez@example.com");
    }

    @Test
    void testGuardar_Exitoso() {
        when(entrenadorRepository.findByNombre(any(String.class))).thenReturn(Optional.empty());
        when(entrenadorRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());
        when(entrenadorRepository.save(any(Entrenador.class))).thenReturn(entrenador);

        Entrenador result = entrenadorService.guardar(entrenadorDTO);

        assertNotNull(result);
        assertEquals(entrenador.getNombre(), result.getNombre());
    }

    @Test
    void testGuardar_NombreDuplicado() {
        when(entrenadorRepository.findByNombre(any(String.class))).thenReturn(Optional.of(entrenador));

        assertThrows(IllegalArgumentException.class, () -> {
            entrenadorService.guardar(entrenadorDTO);
        });
    }

    @Test
    void testFindById_Exitoso() {
        when(entrenadorRepository.findById(1L)).thenReturn(Optional.of(entrenador));

        Optional<Entrenador> result = entrenadorService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(entrenador.getId(), result.get().getId());
    }
}
