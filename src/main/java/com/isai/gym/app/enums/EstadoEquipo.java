package com.isai.gym.app.enums;

public enum EstadoEquipo {
    DISPONIBLE("DISPONIBLE"),
    EN_USO("EN USO"),
    MATENIMIENTO("MANTENIMIENTO"),
    FUERA_SERVICIO("FUERA_SERVICIO");

    private final String displayName;

    EstadoEquipo(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
