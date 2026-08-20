package com.pdv.repository;

import com.pdv.model.DivisaoConta;
import com.pdv.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DivisaoContaRepository extends JpaRepository<DivisaoConta, Long> {

    List<DivisaoConta> findByPedido(Pedido pedido);
}
