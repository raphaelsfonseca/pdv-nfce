package com.pdv.service;

import com.pdv.model.Cliente;
import com.pdv.model.ContaReceber;
import com.pdv.model.Pedido;
import com.pdv.repository.ContaReceberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContaReceberService {

    private final ContaReceberRepository contaReceberRepository;

    public ContaReceberService(ContaReceberRepository contaReceberRepository) {
        this.contaReceberRepository = contaReceberRepository;
    }

    @Transactional
    public ContaReceber criarContaFiado(Cliente cliente, Pedido pedido) {
        ContaReceber conta = new ContaReceber();
        conta.setCliente(cliente);
        conta.setPedido(pedido);
        conta.setValor(pedido.getValorTotal());
        conta.setDataVencimento(LocalDateTime.now().plusDays(30));
        conta.setPaga(false);
        conta.setDataCriacao(LocalDateTime.now());
        conta.setObservacao("Fiado - Pedido #" + pedido.getId());
        return contaReceberRepository.save(conta);
    }

    public List<ContaReceber> listarTodas() {
        return contaReceberRepository.findAllByOrderByDataCriacaoDesc();
    }

    public List<ContaReceber> listarPendentes() {
        return contaReceberRepository.findByPagaOrderByDataCriacaoDesc(false);
    }

    public List<ContaReceber> listarPagas() {
        return contaReceberRepository.findByPagaOrderByDataCriacaoDesc(true);
    }

    public List<ContaReceber> listarPorCliente(Cliente cliente) {
        return contaReceberRepository.findByCliente(cliente);
    }

    public List<ContaReceber> listarPendentesPorCliente(Cliente cliente) {
        return contaReceberRepository.findByClienteAndPaga(cliente, false);
    }

    public BigDecimal totalPendentes() {
        return contaReceberRepository.sumPendentes();
    }

    public BigDecimal totalPendentesPorCliente(Cliente cliente) {
        return contaReceberRepository.sumPendentesByCliente(cliente);
    }

    public BigDecimal totalPagasPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        BigDecimal result = contaReceberRepository.sumPagasByPeriodo(inicio, fim);
        return result != null ? result : BigDecimal.ZERO;
    }

    @Transactional
    public ContaReceber registrarPagamento(Long contaId, BigDecimal valorPago) {
        ContaReceber conta = contaReceberRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com id: " + contaId));
        
        BigDecimal novoValorRecebido = (conta.getValorRecebido() != null ? conta.getValorRecebido() : BigDecimal.ZERO).add(valorPago);
        conta.setValorRecebido(novoValorRecebido);
        
        if (novoValorRecebido.compareTo(conta.getValor()) >= 0) {
            conta.setPaga(true);
            conta.setDataPagamento(LocalDateTime.now());
        }
        
        return contaReceberRepository.save(conta);
    }

    @Transactional
    public ContaReceber registrarPagamento(Long contaId) {
        ContaReceber conta = contaReceberRepository.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada com id: " + contaId));
        conta.setPaga(true);
        conta.setValorRecebido(conta.getValor());
        conta.setDataPagamento(LocalDateTime.now());
        return contaReceberRepository.save(conta);
    }
}
