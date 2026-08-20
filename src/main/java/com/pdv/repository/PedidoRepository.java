package com.pdv.repository;

import com.pdv.enums.StatusPedido;
import com.pdv.model.Mesa;
import com.pdv.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByStatus(StatusPedido status);

    List<Pedido> findByMesaAndStatusIn(Mesa mesa, List<StatusPedido> statuses);

    List<Pedido> findByDataHoraBetween(LocalDateTime start, LocalDateTime end);

    Optional<Pedido> findByNumeroNFCe(Integer numeroNFCe);

    @Query("SELECT COALESCE(SUM(p.valorTotal), 0) FROM Pedido p WHERE p.status = 'FECHADO' AND p.dataHora BETWEEN :inicio AND :fim")
    BigDecimal sumTotalByPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT MAX(p.numeroNFCe) FROM Pedido p WHERE p.numeroNFCe IS NOT NULL")
    Optional<Integer> findMaxNumeroNFCe();
}
