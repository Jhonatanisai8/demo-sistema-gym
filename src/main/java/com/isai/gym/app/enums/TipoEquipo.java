package com.isai.gym.app.enums;

public enum TipoEquipo {
    MAQUINAS_CARDIO("Máquinas de Cardio"),
    MAQUINAS_FUERZA("Máquinas de Fuerza"),
    PESAS_LIBRES("Pesas Libres"),
    ACCESORIOS("Accesorios"),
    COMPLEMENTOS("Complementos"),
    OTROS("Otros");

    private final String displayName;

    TipoEquipo(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
