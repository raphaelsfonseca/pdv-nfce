package com.pdv.model;

import com.pdv.enums.TipoMovimentacao;
import com.pdv.enums.TipoPagamento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes_financeiras")
public class MovimentacaoFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipo;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor = BigDecimal.ZERO;

    private String categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    private TipoPagamento formaPagamento;

    private String observacao;

    public MovimentacaoFinanceira() {}

    public static MovimentacaoFinanceiraBuilder builder() {
        return new MovimentacaoFinanceiraBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public TipoMovimentacao getTipo() { return tipo; }
    public void setTipo(TipoMovimentacao tipo) { this.tipo = tipo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public TipoPagamento getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(TipoPagamento formaPagamento) { this.formaPagamento = formaPagamento; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public static class MovimentacaoFinanceiraBuilder {
        private Long id;
        private LocalDateTime dataHora = LocalDateTime.now();
        private TipoMovimentacao tipo;
        private String descricao;
        private BigDecimal valor = BigDecimal.ZERO;
        private String categoria;
        private Pedido pedido;
        private TipoPagamento formaPagamento;
        private String observacao;

        MovimentacaoFinanceiraBuilder() {}

        public MovimentacaoFinanceiraBuilder id(Long id) { this.id = id; return this; }
        public MovimentacaoFinanceiraBuilder dataHora(LocalDateTime dataHora) { this.dataHora = dataHora; return this; }
        public MovimentacaoFinanceiraBuilder tipo(TipoMovimentacao tipo) { this.tipo = tipo; return this; }
        public MovimentacaoFinanceiraBuilder descricao(String descricao) { this.descricao = descricao; return this; }
        public MovimentacaoFinanceiraBuilder valor(BigDecimal valor) { this.valor = valor; return this; }
        public MovimentacaoFinanceiraBuilder categoria(String categoria) { this.categoria = categoria; return this; }
        public MovimentacaoFinanceiraBuilder pedido(Pedido pedido) { this.pedido = pedido; return this; }
        public MovimentacaoFinanceiraBuilder formaPagamento(TipoPagamento formaPagamento) { this.formaPagamento = formaPagamento; return this; }
        public MovimentacaoFinanceiraBuilder observacao(String observacao) { this.observacao = observacao; return this; }

        public MovimentacaoFinanceira build() {
            MovimentacaoFinanceira m = new MovimentacaoFinanceira();
            m.id = this.id;
            m.dataHora = this.dataHora;
            m.tipo = this.tipo;
            m.descricao = this.descricao;
            m.valor = this.valor;
            m.categoria = this.categoria;
            m.pedido = this.pedido;
            m.formaPagamento = this.formaPagamento;
            m.observacao = this.observacao;
            return m;
        }
    }
}
