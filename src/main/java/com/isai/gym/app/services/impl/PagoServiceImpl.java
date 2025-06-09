package com.isai.gym.app.services.impl;

import com.isai.gym.app.dtos.PagoDTO;
import com.isai.gym.app.entities.Pago;
import com.isai.gym.app.entities.Usuario;
import com.isai.gym.app.repository.PagoRepository;
import com.isai.gym.app.repository.UsuarioRepository;
import com.isai.gym.app.services.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PagoServiceImpl
        implements PagoService {

    private final PagoRepository pagoRepository;

    private final UsuarioRepository usuarioRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");


    @Override
    public PagoDTO registrarPago(PagoDTO pagoDTO) {
        Usuario usuario = usuarioRepository.findById(pagoDTO.getUsuarioId())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con ID: " + pagoDTO.getUsuarioId()));
        pagoDTO.setNombreUsuario(usuario.getNombreCompleto());
        Pago pago = new Pago();
        pago.setUsuario(usuario);
        pago.setConcepto(pagoDTO.getConcepto());
        pago.setMonto(pagoDTO.getMonto());
        pago.setMetodoPago(pagoDTO.getMetodoPago());
        pago.setFechaPago(pagoDTO.getFechaPago());
        pago.setReferenciaId(pagoDTO.getReferenciaId());
        pago.setEstado(pagoDTO.getEstado());
        pago.setObservaciones(pagoDTO.getObservaciones());
        Pago pagoGuardado = pagoRepository.save(pago);
        return mapToDTO(pagoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PagoDTO> obtenerHistorialPagos(Pageable pageable, String keyword) {
        Page<Pago> pagosPage;
        if (keyword != null && !keyword.isEmpty()) {
            pagosPage = pagoRepository.buscarPagosPorKeyword(keyword, pageable);
        } else {
            pagosPage = pagoRepository.findAll(pageable);
        }
        List<PagoDTO> pagoDTOs = pagosPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(pagoDTOs, pageable, pagosPage.getTotalElements());
    }

    @Override
    public Optional<PagoDTO> obtenerPagoPorId(Long id) {
        return pagoRepository.findById(id).map(this::mapToDTO);
    }

    private PagoDTO mapToDTO(Pago pago) {
        PagoDTO dto = new PagoDTO();
        dto.setId(pago.getId());
        dto.setUsuarioId(pago.getUsuario().getId());
        dto.setNombreUsuario(pago.getUsuario().getNombreCompleto());
        dto.setConcepto(pago.getConcepto());
        dto.setMonto(pago.getMonto());
        dto.setMetodoPago(pago.getMetodoPago());
        dto.setFechaPago(pago.getFechaPago());
        dto.setReferenciaId(pago.getReferenciaId());
        dto.setEstado(pago.getEstado());
        dto.setObservaciones(pago.getObservaciones());
        if (pago.getFechaRegistro() != null) {
            dto.setFechaRegistro(pago.getFechaRegistro().format(DATE_TIME_FORMATTER));
        }
        return dto;
    }
}
