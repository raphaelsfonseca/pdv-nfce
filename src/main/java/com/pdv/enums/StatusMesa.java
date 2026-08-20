package com.pdv.enums;

public enum StatusMesa {
    LIVRE("Livre"),
    OCUPADA("Ocupada"),
    RESERVADA("Reservada");

    private final String descricao;

    StatusMesa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
