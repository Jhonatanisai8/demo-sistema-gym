package com.isai.gym.app.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class CarritoDTO {
    private List<CarritoItemDTO> items = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;

    public void recalcularTotal() {
        this.total = items.stream()
                .map(CarritoItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
