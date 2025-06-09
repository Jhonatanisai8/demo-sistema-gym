package com.isai.gym.app.services.impl;

import com.isai.gym.app.dtos.EquipoDTO;
import com.isai.gym.app.entities.Equipo;
import com.isai.gym.app.enums.EstadoEquipo;
import com.isai.gym.app.exceptions.WarehouseException;
import com.isai.gym.app.repository.EquipoRepository;
import com.isai.gym.app.services.EquipoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipoServiceImpl
        implements EquipoService {

    private final EquipoRepository equipoRepository;

    private final AlmacenArchivoImpl almacenArchivo;

    @Override
    public Equipo guardarEquipo(EquipoDTO equipoDTO) {
        if (existeEquipoNombre(equipoDTO.getNombre(), null)) {
            throw new IllegalArgumentException("Ya existe ese equipo registrado.");
        }
        Equipo equipo = new Equipo();
        mapDTOToEntidad(equipoDTO, equipo);
        if (equipoDTO.getFoto() != null
                && !equipoDTO.getFoto().isEmpty()) {
            try {
                String rutaImagen = almacenArchivo.almacenarArchivo(equipoDTO.getFoto());
                equipo.setRutaImagen(rutaImagen);
            } catch (WarehouseException e) {
                throw new RuntimeException("Error al almacenar la imagen del equipo: " + e.getMessage(), e);
            }
        }
        return equipoRepository.save(equipo);
    }

    private static void mapDTOToEntidad(EquipoDTO equipoDTO, Equipo equipo) {
        equipo.setNombre(equipoDTO.getNombre());
        equipo.setDescripcion(equipoDTO.getDescripcion());
        equipo.setCantidadDisponible(equipoDTO.getCantidadDisponible());
        equipo.setEnMantenimiento(equipoDTO.getEnMantenimiento());
        equipo.setTipo(equipoDTO.getTipo());
        equipo.setFechaAdquisicion(equipoDTO.getFechaAdquisicion());
        equipo.setFechaUltimoMantenimiento(equipoDTO.getFechaUltimoMantenimiento());
        equipo.setProximaFechaMantenimiento(equipoDTO.getProximaFechaMantenimiento());
        equipo.setUbicacion(equipoDTO.getUbicacion());
        equipo.setEstado(equipoDTO.getEstado());
        equipo.setNumeroSerie(equipoDTO.getNumeroSerie());
        equipo.setMarca(equipoDTO.getMarca());
        equipo.setModelo(equipoDTO.getModelo());
    }

    @Override
    public Optional<Equipo> obtenerPorId(Long id) {
        return equipoRepository.findById(id);
    }

    @Override
    public Optional<Equipo> actualizarEquipo(Long id, EquipoDTO equipoDTO) {
        return equipoRepository.findById(id).map(equipoExistente -> {
            mapDTOToEntidad(equipoDTO, equipoExistente);
            MultipartFile fotoNueva = equipoDTO.getFoto();
            if (fotoNueva != null && !fotoNueva.isEmpty()) {
                try {
                    if (equipoExistente.getRutaImagen() != null && !equipoExistente.getRutaImagen().isEmpty()) {
                        almacenArchivo.eliminarArchivo(equipoExistente.getRutaImagen());
                    }
                    String nuevaRuta = almacenArchivo.almacenarArchivo(fotoNueva);
                    equipoExistente.setRutaImagen(nuevaRuta);
                } catch (WarehouseException e) {
                    throw new RuntimeException("Error al almacenar/actualizar la imagen del equipo: " + e.getMessage(), e);
                }
            }
            return equipoRepository.save(equipoExistente);
        });
    }

    @Override
    public Page<Equipo> buscarEquiposPorNombre(String nombre, Pageable pageable) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return equipoRepository.findAll(pageable);
        }
        return equipoRepository.findEquipoByNombreContaining(nombre, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Equipo> obtenerEquipos(Pageable pageable) {
        return equipoRepository.findAll(pageable);
    }

    @Override
    public boolean eliminarPorId(Long id) {
        Optional<Equipo> equipoBD = equipoRepository.findById(id);
        if (equipoBD.isPresent()) {
            Equipo equipo = equipoBD.get();
            if (equipo.getRutaImagen() != null &&
                    !equipo.getRutaImagen().isEmpty()) {
                try {
                    almacenArchivo.eliminarArchivo(equipo.getRutaImagen());
                } catch (WarehouseException e) {
                    System.out.println("Error al eliminar la imagen del equipo: " + e.getMessage());
                }
            }
            try {
                equipoRepository.deleteById(id);
                return true;
            } catch (WarehouseException e) {
                System.out.println("Error al eliminar la imagen del equipo: " + id);
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean existeEquipoNombre(String nombre, Long id) {
        Optional<Equipo> existeEquipo = equipoRepository.findByNombre(nombre);
        if (existeEquipo.isPresent()) {
            return id == null || existeEquipo.get().getId().equals(id);
        }
        return false;
    }

    @Override
    public Optional<Equipo> cambiarEstadoEquipo(Long id, EstadoEquipo estadoEquipo) {
        return equipoRepository.findById(id)
                .map(equipo -> {
                    equipo.setEstado(estadoEquipo);
                    return equipoRepository.save(equipo);
                });
    }
}
