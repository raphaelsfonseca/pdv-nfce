package com.pdv.repository;

import com.pdv.enums.TipoMovimentacao;
import com.pdv.model.MovimentacaoFinanceira;
import com.pdv.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentacaoFinanceiraRepository extends JpaRepository<MovimentacaoFinanceira, Long> {

    List<MovimentacaoFinanceira> findByDataHoraBetween(LocalDateTime start, LocalDateTime end);

    List<MovimentacaoFinanceira> findByTipo(TipoMovimentacao tipo);

    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM MovimentacaoFinanceira m WHERE m.tipo = 'ENTRADA' AND m.dataHora BETWEEN :inicio AND :fim")
    BigDecimal sumEntradasByPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COALESCE(SUM(m.valor), 0) FROM MovimentacaoFinanceira m WHERE m.tipo = 'SAIDA' AND m.dataHora BETWEEN :inicio AND :fim")
    BigDecimal sumSaidasByPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    void deleteByPedido(Pedido pedido);
}
