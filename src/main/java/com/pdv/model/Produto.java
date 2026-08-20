package com.pdv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome e obrigatorio")
    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(nullable = false)
    private String unidadeMedida = "UN";

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoCusto = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoVenda = BigDecimal.ZERO;

    private Integer estoqueAtual = 0;

    private Integer estoqueMinimo = 0;

    private String categoria;

    private String foto;

    @Column(nullable = false)
    private boolean ativo = true;

    public Produto() {}

    public Produto(Long id, String nome, String descricao, BigDecimal precoCusto, BigDecimal precoVenda,
                   Integer estoqueAtual, Integer estoqueMinimo, String categoria, String foto, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.unidadeMedida = "UN";
        this.precoCusto = precoCusto;
        this.precoVenda = precoVenda;
        this.estoqueAtual = estoqueAtual;
        this.estoqueMinimo = estoqueMinimo;
        this.categoria = categoria;
        this.foto = foto;
        this.ativo = ativo;
    }

    @jakarta.persistence.Transient
    public BigDecimal getMargemLucro() {
        if (precoCusto.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return precoVenda.subtract(precoCusto)
                .multiply(new BigDecimal("100"))
                .divide(precoCusto, 2, RoundingMode.HALF_UP);
    }

    public static ProdutoBuilder builder() { return new ProdutoBuilder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }
    public BigDecimal getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(BigDecimal precoCusto) { this.precoCusto = precoCusto; }
    public BigDecimal getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(BigDecimal precoVenda) { this.precoVenda = precoVenda; }
    public Integer getEstoqueAtual() { return estoqueAtual; }
    public void setEstoqueAtual(Integer estoqueAtual) { this.estoqueAtual = estoqueAtual; }
    public Integer getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(Integer estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public static class ProdutoBuilder {
        private Long id;
        private String nome;
        private String descricao;
        private String unidadeMedida = "UN";
        private BigDecimal precoCusto = BigDecimal.ZERO;
        private BigDecimal precoVenda = BigDecimal.ZERO;
        private Integer estoqueAtual = 0;
        private Integer estoqueMinimo = 0;
        private String categoria;
        private String foto;
        private boolean ativo = true;

        ProdutoBuilder() {}

        public ProdutoBuilder id(Long id) { this.id = id; return this; }
        public ProdutoBuilder nome(String nome) { this.nome = nome; return this; }
        public ProdutoBuilder descricao(String descricao) { this.descricao = descricao; return this; }
        public ProdutoBuilder unidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; return this; }
        public ProdutoBuilder precoCusto(BigDecimal precoCusto) { this.precoCusto = precoCusto; return this; }
        public ProdutoBuilder precoVenda(BigDecimal precoVenda) { this.precoVenda = precoVenda; return this; }
        public ProdutoBuilder estoqueAtual(Integer estoqueAtual) { this.estoqueAtual = estoqueAtual; return this; }
        public ProdutoBuilder estoqueMinimo(Integer estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; return this; }
        public ProdutoBuilder categoria(String categoria) { this.categoria = categoria; return this; }
        public ProdutoBuilder foto(String foto) { this.foto = foto; return this; }
        public ProdutoBuilder ativo(boolean ativo) { this.ativo = ativo; return this; }

        public Produto build() {
            return new Produto(id, nome, descricao, precoCusto, precoVenda,
                    estoqueAtual, estoqueMinimo, categoria, foto, ativo);
        }
    }
}
