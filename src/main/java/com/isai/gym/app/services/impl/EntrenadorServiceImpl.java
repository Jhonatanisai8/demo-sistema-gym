package com.isai.gym.app.services.impl;

import com.isai.gym.app.dtos.EntrenadorDTO;
import com.isai.gym.app.entities.Entrenador;
import com.isai.gym.app.repository.EntrenadorRepository;
import com.isai.gym.app.services.EntrenadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntrenadorServiceImpl
        implements EntrenadorService {

    private final EntrenadorRepository entrenadorRepository;

    private final AlmacenArchivoImpl almacenArchivo;


    @Override
    @Transactional
    public Entrenador guardar(EntrenadorDTO entrenadorDTO) {
        if (existeNombre(entrenadorDTO.getNombre(), null)) {
            throw new IllegalArgumentException("Nombre ya existe");
        }
        if (entrenadorDTO.getEmail() != null && !entrenadorDTO.getEmail().isEmpty() && existeEmail(entrenadorDTO.getEmail(), null)) {
            throw new IllegalArgumentException("Ya existe un entrenador con ese email.");
        }
        Entrenador entrenador = new Entrenador();
        mapDtoToEntity(entrenadorDTO, entrenador);
        if (entrenadorDTO.getFotoNueva() != null && !entrenadorDTO.getFotoNueva().isEmpty()) {
            String rutaImagen = almacenArchivo.almacenarArchivo(entrenadorDTO.getFotoNueva());
            entrenador.setRutaImagen(rutaImagen);
        }
        return entrenadorRepository.save(entrenador);
    }

    @Override
    @Transactional
    public Optional<Entrenador> actualizar(Long id, EntrenadorDTO entrenadorDTO) {
        return entrenadorRepository.findById(id)
                .map(entrenadorExistente -> {
                    if (existeNombre(entrenadorDTO.getNombre(), id)) {
                        throw new IllegalArgumentException("Nombre ya existe");
                    }
                    if (entrenadorDTO.getEmail() != null && !entrenadorDTO.getEmail().isEmpty() && existeEmail(entrenadorDTO.getEmail(), id)) {
                        throw new IllegalArgumentException("Ya existe un entrenador con ese email.");
                    }
                    mapDtoToEntity(entrenadorDTO, entrenadorExistente);
                    if (entrenadorDTO.getFotoNueva() != null && !entrenadorDTO.getFotoNueva().isEmpty()) {
                        if (entrenadorExistente.getRutaImagen() != null && !entrenadorExistente.getRutaImagen().isEmpty()) {
                            almacenArchivo.eliminarArchivo(entrenadorExistente.getRutaImagen());
                        }
                        String nuevaRutaImagen = almacenArchivo.almacenarArchivo(entrenadorDTO.getFotoNueva());
                        entrenadorExistente.setRutaImagen(nuevaRutaImagen);
                    } else if (entrenadorDTO.getFotoNueva() != null && entrenadorDTO.getFotoNueva().isEmpty() && entrenadorExistente.getRutaImagen() != null && !entrenadorExistente.getRutaImagen().isEmpty()) {
                        almacenArchivo.eliminarArchivo(entrenadorExistente.getRutaImagen());
                        entrenadorExistente.setRutaImagen(entrenadorExistente.getRutaImagen());
                    }
                    return entrenadorRepository.save(entrenadorExistente);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Entrenador> findById(Long id) {
        return entrenadorRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Entrenador> obtenerEntrenadores(Pageable pageable) {
        return entrenadorRepository.findAll(pageable);
    }

    @Override
    public List<Entrenador> obtenerEntrenadores() {
        return entrenadorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Entrenador> buscar(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return entrenadorRepository.findAll(pageable);
        }
        return entrenadorRepository.findByNombreContainingIgnoreCaseOrEspecialidadContainingIgnoreCase(keyword, keyword, pageable);
    }

    @Override
    @Transactional
    public boolean eliminarPorID(Long id) {
        Optional<Entrenador> entrenadorOptional = entrenadorRepository.findById(id);
        if (entrenadorOptional.isPresent()) {
            Entrenador entrenador = entrenadorOptional.get();
            // Antes de eliminar el entrenador, eliminar su imagen si existe
            if (entrenador.getRutaImagen() != null && !entrenador.getRutaImagen().isEmpty()) {
                almacenArchivo.eliminarArchivo(entrenador.getRutaImagen());
            }
            try {
                entrenadorRepository.deleteById(id);
                return true;
            } catch (Exception e) {
                System.err.println("Error al eliminar entrenador con ID " + id + ": " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public Optional<Entrenador> toggleActivo(Long id, boolean activo) {
        return entrenadorRepository.findById(id).map(entrenador -> {
            entrenador.setActivo(activo);
            return entrenadorRepository.save(entrenador);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeNombre(String nombre, Long id) {
        Optional<Entrenador> existingEntrenador = entrenadorRepository.findByNombre(nombre);
        if (existingEntrenador.isPresent()) {
            return id == null || !existingEntrenador.get().getId().equals(id);
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeEmail(String email, Long id) {
        Optional<Entrenador> existingEntrenador = entrenadorRepository.findByEmail(email);
        if (existingEntrenador.isPresent()) {
            return id == null || !existingEntrenador.get().getId().equals(id);
        }
        return false;
    }

    private void mapDtoToEntity(EntrenadorDTO dto, Entrenador entity) {
        entity.setNombre(dto.getNombre());
        entity.setEspecialidad(dto.getEspecialidad());
        entity.setTelefono(dto.getTelefono());
        entity.setEmail(dto.getEmail());
        entity.setFechaContratacion(dto.getFechaContratacion());
        entity.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        entity.setTarifaPorSesion(dto.getTarifaPorSesion());
        entity.setCertificaciones(dto.getCertificaciones());
        entity.setHorarioDisponible(dto.getHorarioDisponible());
    }
}
