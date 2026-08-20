package com.pdv.service;

import com.pdv.model.Produto;
import com.pdv.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public List<Produto> listarAtivos() {
        return produtoRepository.findByAtivo(true);
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Produto> buscarPorCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria);
    }

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public void deletar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
        produto.setAtivo(false);
        produtoRepository.save(produto);
    }

    @Transactional
    public void atualizarEstoque(Long produtoId, int quantidade) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + produtoId));
        if (produto.getEstoqueAtual() < quantidade) {
            throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
        }
        produto.setEstoqueAtual(produto.getEstoqueAtual() - quantidade);
        produtoRepository.save(produto);
    }

    public List<Produto> produtosEstoqueBaixo() {
        return produtoRepository.findEstoqueBaixo();
    }

    public List<String> listarCategorias() {
        return produtoRepository.findAll().stream()
                .map(Produto::getCategoria)
                .filter(c -> c != null && !c.isEmpty())
                .distinct()
                .sorted()
                .toList();
    }

    public long contarAtivos() {
        return produtoRepository.countByAtivo(true);
    }
}
