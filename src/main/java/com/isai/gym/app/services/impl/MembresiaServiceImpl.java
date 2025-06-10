package com.isai.gym.app.services.impl;

import com.isai.gym.app.dtos.MembresiaDTO;
import com.isai.gym.app.entities.Membresia;
import com.isai.gym.app.repository.MembresiaRepository;
import com.isai.gym.app.services.MembresiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembresiaServiceImpl
        implements MembresiaService {
    private final MembresiaRepository membresiaRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Membresia> obtenerMembresias(Pageable pageable) {
        return membresiaRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Membresia guardarMembresia(MembresiaDTO membresiaDTO) {
        if (existeNombre(membresiaDTO.getNombre(), null)) {
            throw new IllegalArgumentException("Ya existe un tipo de membresía con ese nombre.");
        }
        Membresia membresia = new Membresia();
        membresia.setNombre(membresiaDTO.getNombre());
        membresia.setDescripcion(membresiaDTO.getDescripcion());
        membresia.setPrecio(membresiaDTO.getPrecio());
        membresia.setDuracionDias(membresiaDTO.getDuracionDias());
        membresia.setBeneficios(membresiaDTO.getBeneficios());
        membresia.setLimiteAccesosDia(membresiaDTO.getLimiteAccesosDia());
        membresia.setActiva(membresiaDTO.getActiva() != null ? membresiaDTO.getActiva() : true); // Asegura un valor por defecto
        return membresiaRepository.save(membresia);
    }

    @Override
    @Transactional
    public Optional<Membresia> actualizar(Long id, MembresiaDTO membresiaDTO) {
        return membresiaRepository.findById(id).map(membresiaExistente -> {
            if (existeNombre(membresiaDTO.getNombre(), id)) {
                throw new IllegalArgumentException("Ya existe un tipo de membresía con ese nombre.");
            }
            membresiaExistente.setNombre(membresiaDTO.getNombre());
            membresiaExistente.setDescripcion(membresiaDTO.getDescripcion());
            membresiaExistente.setPrecio(membresiaDTO.getPrecio());
            membresiaExistente.setDuracionDias(membresiaDTO.getDuracionDias());
            membresiaExistente.setBeneficios(membresiaDTO.getBeneficios());
            membresiaExistente.setLimiteAccesosDia(membresiaDTO.getLimiteAccesosDia());
            membresiaExistente.setActiva(membresiaDTO.getActiva());
            return membresiaRepository.save(membresiaExistente);
        });
    }

    @Override
    @Transactional(readOnly = true) // Solo lectura, no modifica la base de datos
    public Optional<Membresia> obtenerMembresiaId(Long id) {
        return membresiaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Membresia> buscar(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return membresiaRepository.findAll(pageable);
        }
        return membresiaRepository.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(keyword, keyword, pageable);
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {
        if (membresiaRepository.existsById(id)) {
            try {
                membresiaRepository.deleteById(id);
                return true;
            } catch (Exception e) {
                System.err.println("Error al eliminar el tipo de membresía con ID " + id + ": " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public boolean existeNombre(String nombre, Long id) {
        Optional<Membresia> existingMembresia = membresiaRepository.findByNombre(nombre);
        if (existingMembresia.isPresent()) {
            return id == null || !existingMembresia.get().getId().equals(id);
        }
        return false;
    }

    @Override
    @Transactional
    public Optional<Membresia> cambiarEstado(Long id, boolean activa) {
        return membresiaRepository.findById(id).map(membresia -> {
            membresia.setActiva(activa);
            return membresiaRepository.save(membresia);
        });
    }

    public List<Membresia> obtenerMembresias() {
        return membresiaRepository.findAll();
    }
}
