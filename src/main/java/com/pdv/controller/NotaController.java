package com.pdv.controller;

import com.pdv.model.Pedido;
import com.pdv.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/notas")
public class NotaController {

    private final PedidoService pedidoService;

    public NotaController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) String inicio,
            @RequestParam(required = false) String fim,
            Model model) {
        List<Pedido> notas;
        if (inicio != null && fim != null && !inicio.isEmpty() && !fim.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime dataInicio = LocalDateTime.parse(inicio, formatter);
            LocalDateTime dataFim = LocalDateTime.parse(fim, formatter);
            notas = pedidoService.pedidosHoje().stream()
                    .filter(p -> p.getDataFechamento() != null)
                    .filter(p -> p.getDataFechamento().isAfter(dataInicio) && p.getDataFechamento().isBefore(dataFim))
                    .toList();
        } else {
            notas = pedidoService.pedidosHoje().stream()
                    .filter(p -> p.getDataFechamento() != null)
                    .toList();
        }
        model.addAttribute("notas", notas);
        model.addAttribute("titulo", "Notas Fiscais (NFCe)");
        return "notas";
    }

    @GetMapping("/{id}")
    public String visualizar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Pedido pedido = pedidoService.buscarPorId(id).orElse(null);
        if (pedido == null) {
            redirectAttributes.addFlashAttribute("erro", "Nota não encontrada.");
            return "redirect:/notas";
        }
        model.addAttribute("pedido", pedido);
        return "nota";
    }

    @GetMapping("/{id}/imprimir")
    public String imprimir(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Pedido pedido = pedidoService.buscarPorId(id).orElse(null);
        if (pedido == null) {
            redirectAttributes.addFlashAttribute("erro", "Nota não encontrada.");
            return "redirect:/notas";
        }
        model.addAttribute("pedido", pedido);
        return "nota-impressao";
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pedidoService.deletar(id);
            redirectAttributes.addFlashAttribute("sucesso", "Nota excluída com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir nota: " + e.getMessage());
        }
        return "redirect:/notas";
    }
}
