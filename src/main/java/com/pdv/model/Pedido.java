package com.pdv.model;

import com.pdv.enums.StatusPedido;
import com.pdv.enums.TipoPagamento;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id")
    private Mesa mesa;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status = StatusPedido.ABERTO;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorSubtotal = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private TipoPagamento tipoPagamento;

    private String observacao;

    private Integer numeroNFCe;

    @Column(name = "data_fechamento")
    private LocalDateTime dataFechamento;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DivisaoConta> divisoes = new ArrayList<>();

    public Pedido() {}

    public void calcularTotais() {
        this.valorSubtotal = itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.valorTotal = this.valorSubtotal.subtract(this.valorDesconto);
    }

    public void addItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this);
        calcularTotais();
    }

    public void removerItem(ItemPedido item) {
        itens.remove(item);
        item.setPedido(null);
        calcularTotais();
    }

    public static PedidoBuilder builder() {
        return new PedidoBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
    public BigDecimal getValorSubtotal() { return valorSubtotal; }
    public void setValorSubtotal(BigDecimal valorSubtotal) { this.valorSubtotal = valorSubtotal; }
    public BigDecimal getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(BigDecimal valorDesconto) { this.valorDesconto = valorDesconto; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public TipoPagamento getTipoPagamento() { return tipoPagamento; }
    public void setTipoPagamento(TipoPagamento tipoPagamento) { this.tipoPagamento = tipoPagamento; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public Integer getNumeroNFCe() { return numeroNFCe; }
    public void setNumeroNFCe(Integer numeroNFCe) { this.numeroNFCe = numeroNFCe; }
    public LocalDateTime getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(LocalDateTime dataFechamento) { this.dataFechamento = dataFechamento; }
    public List<DivisaoConta> getDivisoes() { return divisoes; }
    public void setDivisoes(List<DivisaoConta> divisoes) { this.divisoes = divisoes; }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @jakarta.persistence.Transient
    public Long getMesaId() { return mesa != null ? mesa.getId() : null; }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @jakarta.persistence.Transient
    public Integer getMesaNumero() { return mesa != null ? mesa.getNumero() : null; }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @jakarta.persistence.Transient
    public String getClienteNome() { return cliente != null ? cliente.getNome() : null; }

    public static class PedidoBuilder {
        private Long id;
        private Mesa mesa;
        private Cliente cliente;
        private List<ItemPedido> itens = new ArrayList<>();
        private LocalDateTime dataHora = LocalDateTime.now();
        private StatusPedido status = StatusPedido.ABERTO;
        private BigDecimal valorSubtotal = BigDecimal.ZERO;
        private BigDecimal valorDesconto = BigDecimal.ZERO;
        private BigDecimal valorTotal = BigDecimal.ZERO;
        private TipoPagamento tipoPagamento;
        private String observacao;
        private Integer numeroNFCe;
        private LocalDateTime dataFechamento;
        private List<DivisaoConta> divisoes = new ArrayList<>();

        PedidoBuilder() {}

        public PedidoBuilder id(Long id) { this.id = id; return this; }
        public PedidoBuilder mesa(Mesa mesa) { this.mesa = mesa; return this; }
        public PedidoBuilder cliente(Cliente cliente) { this.cliente = cliente; return this; }
        public PedidoBuilder itens(List<ItemPedido> itens) { this.itens = itens; return this; }
        public PedidoBuilder dataHora(LocalDateTime dataHora) { this.dataHora = dataHora; return this; }
        public PedidoBuilder status(StatusPedido status) { this.status = status; return this; }
        public PedidoBuilder valorSubtotal(BigDecimal valorSubtotal) { this.valorSubtotal = valorSubtotal; return this; }
        public PedidoBuilder valorDesconto(BigDecimal valorDesconto) { this.valorDesconto = valorDesconto; return this; }
        public PedidoBuilder valorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; return this; }
        public PedidoBuilder tipoPagamento(TipoPagamento tipoPagamento) { this.tipoPagamento = tipoPagamento; return this; }
        public PedidoBuilder observacao(String observacao) { this.observacao = observacao; return this; }
        public PedidoBuilder numeroNFCe(Integer numeroNFCe) { this.numeroNFCe = numeroNFCe; return this; }
        public PedidoBuilder dataFechamento(LocalDateTime dataFechamento) { this.dataFechamento = dataFechamento; return this; }
        public PedidoBuilder divisoes(List<DivisaoConta> divisoes) { this.divisoes = divisoes; return this; }

        public Pedido build() {
            Pedido pedido = new Pedido();
            pedido.id = this.id;
            pedido.mesa = this.mesa;
            pedido.cliente = this.cliente;
            pedido.itens = this.itens;
            pedido.dataHora = this.dataHora;
            pedido.status = this.status;
            pedido.valorSubtotal = this.valorSubtotal;
            pedido.valorDesconto = this.valorDesconto;
            pedido.valorTotal = this.valorTotal;
            pedido.tipoPagamento = this.tipoPagamento;
            pedido.observacao = this.observacao;
            pedido.numeroNFCe = this.numeroNFCe;
            pedido.dataFechamento = this.dataFechamento;
            pedido.divisoes = this.divisoes;
            return pedido;
        }
    }
}
