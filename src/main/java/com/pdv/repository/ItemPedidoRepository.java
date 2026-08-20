package com.pdv.repository;

import com.pdv.model.ItemPedido;
import com.pdv.model.Pedido;
import com.pdv.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    List<ItemPedido> findByPedido(Pedido pedido);

    List<ItemPedido> findByProduto(Produto produto);
}
