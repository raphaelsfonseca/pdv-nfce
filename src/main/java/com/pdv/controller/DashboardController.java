package com.pdv.controller;

import com.pdv.enums.StatusMesa;
import com.pdv.service.FinanceiroService;
import com.pdv.service.MesaService;
import com.pdv.service.PedidoService;
import com.pdv.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class DashboardController {

    private final ProdutoService produtoService;
    private final PedidoService pedidoService;
    private final MesaService mesaService;
    private final FinanceiroService financeiroService;

    public DashboardController(ProdutoService produtoService, PedidoService pedidoService,
                               MesaService mesaService, FinanceiroService financeiroService) {
        this.produtoService = produtoService;
        this.pedidoService = pedidoService;
        this.mesaService = mesaService;
        this.financeiroService = financeiroService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalProdutos", produtoService.contarAtivos());
        model.addAttribute("pedidosHoje", pedidoService.pedidosHoje().size());
        model.addAttribute("mesasOcupadas", mesaService.contarPorStatus(StatusMesa.OCUPADA));
        model.addAttribute("mesasLivres", mesaService.contarPorStatus(StatusMesa.LIVRE));
        model.addAttribute("lucroHoje", financeiroService.lucroHoje());
        model.addAttribute("totalVendasHoje", financeiroService.totalEntradasHoje());
        model.addAttribute("produtosEstoqueBaixo", produtoService.produtosEstoqueBaixo());
        model.addAttribute("ultimasMovimentacoes", financeiroService.ultimasMovimentacoes(10));
        model.addAttribute("pedidosAbertos", pedidoService.pedidosAbertos());
        model.addAttribute("titulo", "Dashboard");
        return "dashboard";
    }
}
