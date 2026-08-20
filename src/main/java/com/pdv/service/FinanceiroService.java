package com.pdv.service;

import com.pdv.enums.TipoMovimentacao;
import com.pdv.enums.TipoPagamento;
import com.pdv.model.ContaReceber;
import com.pdv.model.MovimentacaoFinanceira;
import com.pdv.repository.ContaReceberRepository;
import com.pdv.repository.MovimentacaoFinanceiraRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FinanceiroService {

    private final MovimentacaoFinanceiraRepository movimentacaoFinanceiraRepository;
    private final ContaReceberRepository contaReceberRepository;

    public FinanceiroService(MovimentacaoFinanceiraRepository movimentacaoFinanceiraRepository,
                             ContaReceberRepository contaReceberRepository) {
        this.movimentacaoFinanceiraRepository = movimentacaoFinanceiraRepository;
        this.contaReceberRepository = contaReceberRepository;
    }

    public List<MovimentacaoFinanceira> listarTodas() {
        return movimentacaoFinanceiraRepository.findAll();
    }

    public List<MovimentacaoFinanceira> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return movimentacaoFinanceiraRepository.findByDataHoraBetween(inicio, fim);
    }

    public BigDecimal totalEntradasHoje() {
        LocalDateTime inicio = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime fim = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);
        return movimentacaoFinanceiraRepository.sumEntradasByPeriodo(inicio, fim);
    }

    public BigDecimal totalSaidasHoje() {
        LocalDateTime inicio = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime fim = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);
        return movimentacaoFinanceiraRepository.sumSaidasByPeriodo(inicio, fim);
    }

    public BigDecimal lucroHoje() {
        return totalEntradasHoje().subtract(totalSaidasHoje());
    }

    public BigDecimal totalEntradasPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return movimentacaoFinanceiraRepository.sumEntradasByPeriodo(inicio, fim);
    }

    public BigDecimal totalSaidasPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return movimentacaoFinanceiraRepository.sumSaidasByPeriodo(inicio, fim);
    }

    public MovimentacaoFinanceira registrarSaida(String descricao, BigDecimal valor, String categoria) {
        MovimentacaoFinanceira movimentacao = new MovimentacaoFinanceira();
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setTipo(TipoMovimentacao.SAIDA);
        movimentacao.setDescricao(descricao);
        movimentacao.setValor(valor);
        movimentacao.setCategoria(categoria);
        return movimentacaoFinanceiraRepository.save(movimentacao);
    }

    public MovimentacaoFinanceira registrarEntrada(String descricao, BigDecimal valor, String categoria, TipoPagamento formaPagamento) {
        MovimentacaoFinanceira movimentacao = new MovimentacaoFinanceira();
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setTipo(TipoMovimentacao.ENTRADA);
        movimentacao.setDescricao(descricao);
        movimentacao.setValor(valor);
        movimentacao.setCategoria(categoria);
        movimentacao.setFormaPagamento(formaPagamento);
        return movimentacaoFinanceiraRepository.save(movimentacao);
    }

    public Map<String, BigDecimal> resumoPorCategoria(LocalDateTime inicio, LocalDateTime fim) {
        List<MovimentacaoFinanceira> movimentacoes = movimentacaoFinanceiraRepository.findByDataHoraBetween(inicio, fim);
        return movimentacoes.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getCategoria() != null ? m.getCategoria() : "Sem categoria",
                        LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, MovimentacaoFinanceira::getValor, BigDecimal::add)
                ));
    }

    public List<MovimentacaoFinanceira> ultimasMovimentacoes(int limit) {
        return movimentacaoFinanceiraRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "dataHora"))
        ).getContent();
    }

    public List<ContaReceber> contasReceberPendentes() {
        return contaReceberRepository.findByPagaOrderByDataCriacaoDesc(false);
    }

    public List<ContaReceber> todasContasReceber() {
        return contaReceberRepository.findAllByOrderByDataCriacaoDesc();
    }

    public BigDecimal totalContasReceberPendentes() {
        BigDecimal result = contaReceberRepository.sumPendentes();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Transactional
    public void pagarContaReceber(Long contaId) {
        ContaReceber conta = contaReceberRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com id: " + contaId));
        conta.setPaga(true);
        conta.setValorRecebido(conta.getValor());
        conta.setDataPagamento(LocalDateTime.now());
        contaReceberRepository.save(conta);

        MovimentacaoFinanceira movimentacao = new MovimentacaoFinanceira();
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setTipo(TipoMovimentacao.ENTRADA);
        movimentacao.setDescricao("Pagamento fiado - " + conta.getCliente().getNome() + " (Pedido #" + conta.getPedido().getId() + ")");
        movimentacao.setValor(conta.getValor());
        movimentacao.setCategoria("Fiado");
        movimentacao.setFormaPagamento(TipoPagamento.FIADO);
        movimentacaoFinanceiraRepository.save(movimentacao);
    }

    @Transactional
    public void pagarContaReceberParcial(Long contaId, BigDecimal valorPago) {
        ContaReceber conta = contaReceberRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com id: " + contaId));

        BigDecimal novoValorRecebido = (conta.getValorRecebido() != null ? conta.getValorRecebido() : BigDecimal.ZERO).add(valorPago);
        conta.setValorRecebido(novoValorRecebido);

        boolean totalmentePaga = novoValorRecebido.compareTo(conta.getValor()) >= 0;
        if (totalmentePaga) {
            conta.setPaga(true);
            conta.setDataPagamento(LocalDateTime.now());
        }
        contaReceberRepository.save(conta);

        MovimentacaoFinanceira movimentacao = new MovimentacaoFinanceira();
        movimentacao.setDataHora(LocalDateTime.now());
        movimentacao.setTipo(TipoMovimentacao.ENTRADA);
        movimentacao.setDescricao("Pagamento parcial fiado - " + conta.getCliente().getNome() + " (Pedido #" + conta.getPedido().getId() + ")");
        movimentacao.setValor(valorPago);
        movimentacao.setCategoria("Fiado");
        movimentacao.setFormaPagamento(TipoPagamento.FIADO);
        movimentacaoFinanceiraRepository.save(movimentacao);
    }
}
