package com.pdv.controller;

import com.pdv.enums.TipoPagamento;
import com.pdv.service.FinanceiroService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
@RequestMapping("/financeiro")
public class FinanceiroController {

    private final FinanceiroService financeiroService;

    public FinanceiroController(FinanceiroService financeiroService) {
        this.financeiroService = financeiroService;
    }

    @GetMapping
    public String financeiro(
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fim,
            Model model) {
        LocalDateTime dataInicio;
        LocalDateTime dataFim;

        if (inicio != null && fim != null && !inicio.isEmpty() && !fim.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            dataInicio = LocalDateTime.parse(inicio, formatter);
            dataFim = LocalDateTime.parse(fim, formatter);
        } else {
            dataInicio = LocalDate.now().atStartOfDay();
            dataFim = LocalDateTime.now();
        }

        model.addAttribute("totalEntradasHoje", financeiroService.totalEntradasHoje());
        model.addAttribute("totalSaidasHoje", financeiroService.totalSaidasHoje());
        model.addAttribute("lucroHoje", financeiroService.lucroHoje());
        model.addAttribute("totalEntradasPeriodo", financeiroService.totalEntradasPeriodo(dataInicio, dataFim));
        model.addAttribute("totalSaidasPeriodo", financeiroService.totalSaidasPeriodo(dataInicio, dataFim));
        model.addAttribute("movimentacoes", financeiroService.ultimasMovimentacoes(50));
        model.addAttribute("resumoPorCategoria", financeiroService.resumoPorCategoria(dataInicio, dataFim));
        model.addAttribute("contasReceber", financeiroService.todasContasReceber());
        model.addAttribute("totalContasReceberPendentes", financeiroService.totalContasReceberPendentes());
        model.addAttribute("titulo", "Financeiro");
        return "financeiro";
    }

    @PostMapping("/saida")
    public String registrarSaida(
            @RequestParam String descricao,
            @RequestParam BigDecimal valor,
            @RequestParam(required = false) String categoria,
            RedirectAttributes redirectAttributes) {
        try {
            financeiroService.registrarSaida(descricao, valor, categoria);
            redirectAttributes.addFlashAttribute("sucesso", "Saída registrada com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao registrar saída: " + e.getMessage());
        }
        return "redirect:/financeiro";
    }

    @PostMapping("/entrada")
    public String registrarEntrada(
            @RequestParam String descricao,
            @RequestParam BigDecimal valor,
            @RequestParam(required = false) String categoria,
            @RequestParam TipoPagamento tipoPagamento,
            RedirectAttributes redirectAttributes) {
        try {
            financeiroService.registrarEntrada(descricao, valor, categoria, tipoPagamento);
            redirectAttributes.addFlashAttribute("sucesso", "Entrada registrada com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao registrar entrada: " + e.getMessage());
        }
        return "redirect:/financeiro";
    }

    @PostMapping("/conta-receber/{id}/pagar")
    public String pagarContaReceber(
            @PathVariable Long id,
            @RequestParam(required = false) BigDecimal valor,
            RedirectAttributes redirectAttributes) {
        try {
            if (valor != null && valor.compareTo(BigDecimal.ZERO) > 0) {
                financeiroService.pagarContaReceberParcial(id, valor);
            } else {
                financeiroService.pagarContaReceber(id);
            }
            redirectAttributes.addFlashAttribute("sucesso", "Pagamento registrado com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao registrar pagamento: " + e.getMessage());
        }
        return "redirect:/financeiro";
    }

    @GetMapping("/api/resumo")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> resumo() {
        try {
            LocalDateTime inicioDoDia = LocalDate.now().atStartOfDay();
            LocalDateTime agora = LocalDateTime.now();
            Map<String, Object> resumo = Map.of(
                    "totalEntradasHoje", financeiroService.totalEntradasHoje(),
                    "totalSaidasHoje", financeiroService.totalSaidasHoje(),
                    "lucroHoje", financeiroService.lucroHoje(),
                    "resumoPorCategoria", financeiroService.resumoPorCategoria(inicioDoDia, agora)
            );
            return ResponseEntity.ok(resumo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/movimentacoes")
    @ResponseBody
    public ResponseEntity<?> movimentacoes() {
        try {
            return ResponseEntity.ok(financeiroService.ultimasMovimentacoes(50));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
