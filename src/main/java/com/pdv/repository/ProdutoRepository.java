package com.pdv.repository;

import com.pdv.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    List<Produto> findByCategoria(String categoria);

    List<Produto> findByAtivo(boolean ativo);

    @Query("SELECT p FROM Produto p WHERE p.estoqueAtual < p.estoqueMinimo AND p.ativo = true")
    List<Produto> findEstoqueBaixo();

    long countByAtivo(boolean ativo);
}
