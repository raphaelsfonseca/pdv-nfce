package com.pdv.model;

import com.pdv.enums.StatusMesa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mesas")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer numero;

    private Integer capacidade = 4;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMesa status = StatusMesa.LIVRE;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pedido_atual_id")
    private Pedido pedidoAtual;

    private String observacao;

    public Mesa() {}

    public Mesa(Long id, Integer numero, Integer capacidade, StatusMesa status,
                Pedido pedidoAtual, String observacao) {
        this.id = id;
        this.numero = numero;
        this.capacidade = capacidade;
        this.status = status;
        this.pedidoAtual = pedidoAtual;
        this.observacao = observacao;
    }

    public static MesaBuilder builder() {
        return new MesaBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }
    public StatusMesa getStatus() { return status; }
    public void setStatus(StatusMesa status) { this.status = status; }
    public Pedido getPedidoAtual() { return pedidoAtual; }
    public void setPedidoAtual(Pedido pedidoAtual) { this.pedidoAtual = pedidoAtual; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public static class MesaBuilder {
        private Long id;
        private Integer numero;
        private Integer capacidade = 4;
        private StatusMesa status = StatusMesa.LIVRE;
        private Pedido pedidoAtual;
        private String observacao;

        MesaBuilder() {}

        public MesaBuilder id(Long id) { this.id = id; return this; }
        public MesaBuilder numero(Integer numero) { this.numero = numero; return this; }
        public MesaBuilder capacidade(Integer capacidade) { this.capacidade = capacidade; return this; }
        public MesaBuilder status(StatusMesa status) { this.status = status; return this; }
        public MesaBuilder pedidoAtual(Pedido pedidoAtual) { this.pedidoAtual = pedidoAtual; return this; }
        public MesaBuilder observacao(String observacao) { this.observacao = observacao; return this; }

        public Mesa build() {
            return new Mesa(id, numero, capacidade, status, pedidoAtual, observacao);
        }
    }
}
