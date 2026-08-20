package com.pdv.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade = 1;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    private String observacao;

    public ItemPedido() {}

    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        this.subtotal = this.precoUnitario
                .multiply(new BigDecimal(this.quantidade))
                .subtract(this.desconto);
    }

    public static ItemPedidoBuilder builder() {
        return new ItemPedidoBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }
    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto = desconto; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public static class ItemPedidoBuilder {
        private Long id;
        private Pedido pedido;
        private Produto produto;
        private Integer quantidade = 1;
        private BigDecimal precoUnitario = BigDecimal.ZERO;
        private BigDecimal desconto = BigDecimal.ZERO;
        private BigDecimal subtotal = BigDecimal.ZERO;
        private String observacao;

        ItemPedidoBuilder() {}

        public ItemPedidoBuilder id(Long id) { this.id = id; return this; }
        public ItemPedidoBuilder pedido(Pedido pedido) { this.pedido = pedido; return this; }
        public ItemPedidoBuilder produto(Produto produto) { this.produto = produto; return this; }
        public ItemPedidoBuilder quantidade(Integer quantidade) { this.quantidade = quantidade; return this; }
        public ItemPedidoBuilder precoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; return this; }
        public ItemPedidoBuilder desconto(BigDecimal desconto) { this.desconto = desconto; return this; }
        public ItemPedidoBuilder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
        public ItemPedidoBuilder observacao(String observacao) { this.observacao = observacao; return this; }

        public ItemPedido build() {
            ItemPedido item = new ItemPedido();
            item.id = this.id;
            item.pedido = this.pedido;
            item.produto = this.produto;
            item.quantidade = this.quantidade;
            item.precoUnitario = this.precoUnitario;
            item.desconto = this.desconto;
            item.subtotal = this.subtotal;
            item.observacao = this.observacao;
            return item;
        }
    }
}
