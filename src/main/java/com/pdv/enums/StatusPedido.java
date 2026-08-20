package com.pdv.enums;

public enum StatusPedido {
    ABERTO("Aberto"),
    EM_PREPARO("Em Preparo"),
    PRONTO("Pronto"),
    ENTREGUE("Entregue"),
    FECHADO("Fechado"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
