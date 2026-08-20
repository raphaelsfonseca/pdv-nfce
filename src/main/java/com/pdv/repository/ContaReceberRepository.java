package com.pdv.repository;

import com.pdv.model.Cliente;
import com.pdv.model.ContaReceber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContaReceberRepository extends JpaRepository<ContaReceber, Long> {

    List<ContaReceber> findByCliente(Cliente cliente);

    List<ContaReceber> findByPaga(boolean paga);

    List<ContaReceber> findByPagaOrderByDataCriacaoDesc(boolean paga);

    List<ContaReceber> findAllByOrderByDataCriacaoDesc();

    @Query("SELECT COALESCE(SUM(c.valor), 0) FROM ContaReceber c WHERE c.paga = false")
    BigDecimal sumPendentes();

    @Query("SELECT COALESCE(SUM(c.valor), 0) FROM ContaReceber c WHERE c.paga = false AND c.cliente = :cliente")
    BigDecimal sumPendentesByCliente(@Param("cliente") Cliente cliente);

    @Query("SELECT COALESCE(SUM(c.valor), 0) FROM ContaReceber c WHERE c.paga = true AND c.dataPagamento BETWEEN :inicio AND :fim")
    BigDecimal sumPagasByPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    List<ContaReceber> findByClienteAndPaga(Cliente cliente, boolean paga);
}
