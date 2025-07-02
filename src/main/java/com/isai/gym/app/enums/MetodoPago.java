package com.isai.gym.app.enums;

public enum MetodoPago {
    TARJETA_CREDITO("Tarjeta de Crédito"),
    TRANSFERENCIA_BANCARIA("Transferencia Bancaria"),
    EFECTIVO("Efectivo");

    private final String displayName;

    MetodoPago(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
