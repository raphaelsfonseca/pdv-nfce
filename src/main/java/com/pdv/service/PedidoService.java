package com.pdv.service;

import com.pdv.enums.StatusMesa;
import com.pdv.enums.StatusPedido;
import com.pdv.enums.TipoMovimentacao;
import com.pdv.enums.TipoPagamento;
import com.pdv.model.*;
import com.pdv.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final DivisaoContaRepository divisaoContaRepository;
    private final MesaRepository mesaRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final MovimentacaoFinanceiraRepository movimentacaoFinanceiraRepository;
    private final ContaReceberService contaReceberService;

    public PedidoService(PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository,
                         DivisaoContaRepository divisaoContaRepository, MesaRepository mesaRepository,
                         ClienteRepository clienteRepository, ProdutoRepository produtoRepository,
                         MovimentacaoFinanceiraRepository movimentacaoFinanceiraRepository,
                         ContaReceberService contaReceberService) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.divisaoContaRepository = divisaoContaRepository;
        this.mesaRepository = mesaRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.movimentacaoFinanceiraRepository = movimentacaoFinanceiraRepository;
        this.contaReceberService = contaReceberService;
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    @Transactional
    public Pedido criarPedido(Long mesaId, Long clienteId) {
        Cliente cliente = null;
        if (clienteId != null) {
            cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + clienteId));
        }

        Pedido pedido = new Pedido();
        if (mesaId != null) {
            Mesa mesa = mesaRepository.findById(mesaId)
                    .orElseThrow(() -> new RuntimeException("Mesa não encontrada com id: " + mesaId));
            pedido.setMesa(mesa);
            mesa.setStatus(StatusMesa.OCUPADA);
            mesa.setPedidoAtual(pedido);
            mesaRepository.save(mesa);
        }
        pedido.setCliente(cliente);
        pedido.setDataHora(LocalDateTime.now());
        pedido.setStatus(StatusPedido.ABERTO);
        pedido.setValorSubtotal(BigDecimal.ZERO);
        pedido.setValorDesconto(BigDecimal.ZERO);
        pedido.setValorTotal(BigDecimal.ZERO);
        pedido.setItens(new ArrayList<>());
        pedido.setDivisoes(new ArrayList<>());

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido adicionarItem(Long pedidoId, Long produtoId, int quantidade) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + pedidoId));

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + produtoId));

        if (produto.getEstoqueAtual() < quantidade) {
            throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        ItemPedido item = new ItemPedido();
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(produto.getPrecoVenda());
        item.setDesconto(BigDecimal.ZERO);
        item.setSubtotal(produto.getPrecoVenda().multiply(BigDecimal.valueOf(quantidade)));
        item = itemPedidoRepository.save(item);

        pedido.getItens().add(item);

        BigDecimal subtotalItens = pedido.getItens().stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setValorSubtotal(subtotalItens);
        pedido.setValorTotal(subtotalItens.subtract(pedido.getValorDesconto()));

        produto.setEstoqueAtual(produto.getEstoqueAtual() - quantidade);
        produtoRepository.save(produto);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido removerItem(Long pedidoId, Long itemId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + pedidoId));

        ItemPedido item = itemPedidoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado com id: " + itemId));

        if (!item.getPedido().getId().equals(pedidoId)) {
            throw new RuntimeException("Item não pertence ao pedido informado");
        }

        Produto produto = item.getProduto();
        produto.setEstoqueAtual(produto.getEstoqueAtual() + item.getQuantidade());
        produtoRepository.save(produto);

        pedido.getItens().remove(item);
        itemPedidoRepository.delete(item);

        BigDecimal subtotalItens = pedido.getItens().stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setValorSubtotal(subtotalItens);
        pedido.setValorTotal(subtotalItens.subtract(pedido.getValorDesconto()));

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atualizarQuantidadeItem(Long pedidoId, Long itemId, int novaQuantidade) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + pedidoId));

        ItemPedido item = itemPedidoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado com id: " + itemId));

        if (!item.getPedido().getId().equals(pedidoId)) {
            throw new RuntimeException("Item não pertence ao pedido informado");
        }

        Produto produto = item.getProduto();
        int diferenca = novaQuantidade - item.getQuantidade();

        if (diferenca > 0 && produto.getEstoqueAtual() < diferenca) {
            throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        produto.setEstoqueAtual(produto.getEstoqueAtual() - diferenca);
        produtoRepository.save(produto);

        item.setQuantidade(novaQuantidade);
        item.setSubtotal(item.getPrecoUnitario().multiply(BigDecimal.valueOf(novaQuantidade)));
        itemPedidoRepository.save(item);

        BigDecimal subtotalItens = pedido.getItens().stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setValorSubtotal(subtotalItens);
        pedido.setValorTotal(subtotalItens.subtract(pedido.getValorDesconto()));

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido fecharPedido(Long pedidoId, TipoPagamento tipoPagamento) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + pedidoId));

        if (tipoPagamento == TipoPagamento.FIADO && (pedido.getCliente() == null)) {
            throw new RuntimeException("Para venda fiado é necessário selecionar um cliente cadastrado.");
        }

        pedido.setStatus(StatusPedido.FECHADO);
        pedido.setTipoPagamento(tipoPagamento);
        pedido.setDataFechamento(LocalDateTime.now());
        pedido.setNumeroNFCe(proximoNumeroNFCe());

        if (tipoPagamento != TipoPagamento.FIADO) {
            MovimentacaoFinanceira movimentacao = new MovimentacaoFinanceira();
            movimentacao.setDataHora(LocalDateTime.now());
            movimentacao.setTipo(TipoMovimentacao.ENTRADA);
            movimentacao.setDescricao("Pedido #" + pedido.getId() + " - Pagamento");
            movimentacao.setValor(pedido.getValorTotal());
            movimentacao.setCategoria("Vendas");
            movimentacao.setPedido(pedido);
            movimentacao.setFormaPagamento(tipoPagamento);
            movimentacaoFinanceiraRepository.save(movimentacao);
        } else {
            contaReceberService.criarContaFiado(pedido.getCliente(), pedido);
        }

        Mesa mesa = pedido.getMesa();
        if (mesa != null) {
            mesa.setStatus(StatusMesa.LIVRE);
            mesa.setPedidoAtual(null);
            mesaRepository.save(mesa);
        }

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido cancelarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + pedidoId));

        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoqueAtual(produto.getEstoqueAtual() + item.getQuantidade());
            produtoRepository.save(produto);
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setDataFechamento(LocalDateTime.now());

        Mesa mesa = pedido.getMesa();
        if (mesa != null) {
            mesa.setStatus(StatusMesa.LIVRE);
            mesa.setPedidoAtual(null);
            mesaRepository.save(mesa);
        }

        return pedidoRepository.save(pedido);
    }

    public Pedido aplicarDesconto(Long pedidoId, BigDecimal desconto) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + pedidoId));

        pedido.setValorDesconto(desconto);
        pedido.setValorTotal(pedido.getValorSubtotal().subtract(desconto));

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> pedidosAbertos() {
        return pedidoRepository.findByStatus(StatusPedido.ABERTO);
    }

    public List<Pedido> pedidosHoje() {
        return pedidoRepository.findByDataHoraBetween(
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                LocalDateTime.now().toLocalDate().atTime(23, 59, 59)
        );
    }

    public int proximoNumeroNFCe() {
        return pedidoRepository.findMaxNumeroNFCe()
                .map(max -> max + 1)
                .orElse(1);
    }

    @Transactional
    public DivisaoConta dividirConta(Long pedidoId, int numPessoas) {
        if (numPessoas <= 0) {
            throw new IllegalArgumentException("Número de pessoas deve ser maior que zero");
        }

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + pedidoId));

        List<DivisaoConta> divisoesExistentes = divisaoContaRepository.findByPedido(pedido);
        divisoesExistentes.forEach(d -> {
            d.getItens().clear();
            divisaoContaRepository.delete(d);
        });

        BigDecimal valorPorPessoa = pedido.getValorTotal()
                .divide(BigDecimal.valueOf(numPessoas), 2, RoundingMode.CEILING);

        for (int i = 1; i <= numPessoas; i++) {
            DivisaoConta divisao = new DivisaoConta();
            divisao.setPedido(pedido);
            divisao.setNumeroParte(i);
            divisao.setPago(false);
            divisao.setItens(new ArrayList<>());

            if (i == numPessoas) {
                BigDecimal somaAnteriores = valorPorPessoa.multiply(BigDecimal.valueOf(numPessoas - 1));
                divisao.setValor(pedido.getValorTotal().subtract(somaAnteriores));
            } else {
                divisao.setValor(valorPorPessoa);
            }

            divisaoContaRepository.save(divisao);
        }

        return divisaoContaRepository.findByPedido(pedido).get(0);
    }

    @Transactional
    public void pagamentoDividido(Long divisaoId) {
        DivisaoConta divisao = divisaoContaRepository.findById(divisaoId)
                .orElseThrow(() -> new RuntimeException("Divisão não encontrada com id: " + divisaoId));

        divisao.setPago(true);
        divisaoContaRepository.save(divisao);

        List<DivisaoConta> divisoes = divisaoContaRepository.findByPedido(divisao.getPedido());
        boolean todasPagas = divisoes.stream().allMatch(DivisaoConta::isPago);

        if (todasPagas) {
            Pedido pedido = divisao.getPedido();
            pedido.setStatus(StatusPedido.FECHADO);
            pedido.setDataFechamento(LocalDateTime.now());

            Mesa mesa = pedido.getMesa();
            if (mesa != null) {
                mesa.setStatus(StatusMesa.LIVRE);
                mesa.setPedidoAtual(null);
                mesaRepository.save(mesa);
            }

            pedidoRepository.save(pedido);
        }
    }

    public Pedido salvar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atribuirCliente(Long pedidoId, Long clienteId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + pedidoId));
        if (clienteId != null) {
            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + clienteId));
            pedido.setCliente(cliente);
        } else {
            pedido.setCliente(null);
        }
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atribuirMesa(Long pedidoId, Long mesaId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + pedidoId));
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RuntimeException("Mesa não encontrada com id: " + mesaId));
        if (pedido.getMesa() != null) {
            Mesa antiga = pedido.getMesa();
            if (!antiga.getId().equals(mesaId)) {
                antiga.setStatus(StatusMesa.LIVRE);
                antiga.setPedidoAtual(null);
                mesaRepository.save(antiga);
            }
        }
        pedido.setMesa(mesa);
        mesa.setStatus(StatusMesa.OCUPADA);
        mesa.setPedidoAtual(pedido);
        mesaRepository.save(mesa);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido desatribuirMesa(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + pedidoId));
        Mesa mesa = pedido.getMesa();
        if (mesa != null) {
            mesa.setStatus(StatusMesa.LIVRE);
            mesa.setPedidoAtual(null);
            mesaRepository.save(mesa);
            pedido.setMesa(null);
        }
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void deletar(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + pedidoId));
        if (pedido.getMesa() != null) {
            Mesa mesa = pedido.getMesa();
            mesa.setStatus(StatusMesa.LIVRE);
            mesa.setPedidoAtual(null);
            mesaRepository.save(mesa);
        }
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoqueAtual(produto.getEstoqueAtual() + item.getQuantidade());
            produtoRepository.save(produto);
        }
        movimentacaoFinanceiraRepository.deleteByPedido(pedido);
        pedidoRepository.delete(pedido);
    }
}
