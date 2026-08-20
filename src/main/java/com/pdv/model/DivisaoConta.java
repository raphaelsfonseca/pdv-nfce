package com.pdv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "divisoes_conta")
public class DivisaoConta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(nullable = false)
    private Integer numeroParte;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor = BigDecimal.ZERO;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "divisao_conta_itens",
            joinColumns = @JoinColumn(name = "divisao_id"),
            inverseJoinColumns = @JoinColumn(name = "item_pedido_id")
    )
    private List<ItemPedido> itens = new ArrayList<>();

    @Column(nullable = false)
    private boolean pago = false;

    public DivisaoConta() {}

    public static DivisaoContaBuilder builder() {
        return new DivisaoContaBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public Integer getNumeroParte() { return numeroParte; }
    public void setNumeroParte(Integer numeroParte) { this.numeroParte = numeroParte; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }
    public boolean isPago() { return pago; }
    public void setPago(boolean pago) { this.pago = pago; }

    public static class DivisaoContaBuilder {
        private Long id;
        private Pedido pedido;
        private Integer numeroParte;
        private BigDecimal valor = BigDecimal.ZERO;
        private List<ItemPedido> itens = new ArrayList<>();
        private boolean pago = false;

        DivisaoContaBuilder() {}

        public DivisaoContaBuilder id(Long id) { this.id = id; return this; }
        public DivisaoContaBuilder pedido(Pedido pedido) { this.pedido = pedido; return this; }
        public DivisaoContaBuilder numeroParte(Integer numeroParte) { this.numeroParte = numeroParte; return this; }
        public DivisaoContaBuilder valor(BigDecimal valor) { this.valor = valor; return this; }
        public DivisaoContaBuilder itens(List<ItemPedido> itens) { this.itens = itens; return this; }
        public DivisaoContaBuilder pago(boolean pago) { this.pago = pago; return this; }

        public DivisaoConta build() {
            DivisaoConta d = new DivisaoConta();
            d.id = this.id;
            d.pedido = this.pedido;
            d.numeroParte = this.numeroParte;
            d.valor = this.valor;
            d.itens = this.itens;
            d.pago = this.pago;
            return d;
        }
    }
}
