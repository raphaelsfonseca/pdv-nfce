package com.pdv.controller;

import com.pdv.enums.TipoPagamento;
import com.pdv.model.Cliente;
import com.pdv.model.Pedido;
import com.pdv.service.ClienteService;
import com.pdv.service.MesaService;
import com.pdv.service.PedidoService;
import com.pdv.service.ProdutoService;
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

import java.util.List;

@Controller
@RequestMapping("/pdv")
public class PedidoController {

    private final PedidoService pedidoService;
    private final MesaService mesaService;
    private final ProdutoService produtoService;
    private final ClienteService clienteService;

    public PedidoController(PedidoService pedidoService, MesaService mesaService,
                            ProdutoService produtoService, ClienteService clienteService) {
        this.pedidoService = pedidoService;
        this.mesaService = mesaService;
        this.produtoService = produtoService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String pdv(@RequestParam(required = false) Long pedidoId,
                      @RequestParam(required = false) Long mesaId,
                      @RequestParam(required = false) Long clienteId,
                      @RequestParam(required = false) String novoClienteNome,
                      Model model) {
        model.addAttribute("mesas", mesaService.listarTodas());
        model.addAttribute("produtos", produtoService.listarAtivos());
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("categorias", produtoService.listarCategorias());

        if (pedidoId != null) {
            Pedido pedido = pedidoService.buscarPorId(pedidoId).orElse(null);
            model.addAttribute("pedidoAberto", pedido);
        } else if (mesaId != null) {
            try {
                Long finalClienteId = clienteId;
                if (novoClienteNome != null && !novoClienteNome.trim().isEmpty()) {
                    Cliente novoCliente = new Cliente();
                    novoCliente.setNome(novoClienteNome.trim());
                    novoCliente.setAtivo(true);
                    novoCliente = clienteService.salvar(novoCliente);
                    finalClienteId = novoCliente.getId();
                }
                Pedido pedido = pedidoService.criarPedido(mesaId, finalClienteId);
                model.addAttribute("pedidoAberto", pedido);
            } catch (Exception e) {
                model.addAttribute("pedidoAberto", null);
            }
        } else {
            model.addAttribute("pedidoAberto", null);
        }

        return "pdv";
    }

    @PostMapping("/novo")
    public String novoPedido(
            @RequestParam Long mesaId,
            @RequestParam(required = false) Long clienteId,
            RedirectAttributes redirectAttributes) {
        try {
            Pedido pedido = pedidoService.criarPedido(mesaId, clienteId);
            redirectAttributes.addAttribute("pedidoId", pedido.getId());
            return "redirect:/pdv";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao criar pedido: " + e.getMessage());
            return "redirect:/pdv";
        }
    }

    @PostMapping("/novo-pedido")
    public String novoPedidoBalcao(RedirectAttributes redirectAttributes) {
        try {
            Pedido pedido = pedidoService.criarPedido(null, null);
            redirectAttributes.addAttribute("pedidoId", pedido.getId());
            return "redirect:/pdv";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao criar pedido: " + e.getMessage());
            return "redirect:/pdv";
        }
    }

    @PostMapping("/novo-pedido-com-item")
    public String novoPedidoComItem(
            @RequestParam Long produtoId,
            @RequestParam int quantidade,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long mesaId,
            RedirectAttributes redirectAttributes) {
        try {
            Pedido pedido = pedidoService.criarPedido(mesaId, clienteId);
            pedidoService.adicionarItem(pedido.getId(), produtoId, quantidade);
            redirectAttributes.addAttribute("pedidoId", pedido.getId());
            return "redirect:/pdv";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao criar pedido: " + e.getMessage());
            return "redirect:/pdv";
        }
    }

    @PostMapping("/{pedidoId}/atribuir-cliente")
    public String atribuirCliente(
            @PathVariable Long pedidoId,
            @RequestParam(required = false) Long clienteId,
            RedirectAttributes redirectAttributes) {
        try {
            pedidoService.atribuirCliente(pedidoId, clienteId);
            redirectAttributes.addAttribute("pedidoId", pedidoId);
            return "redirect:/pdv";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atribuir cliente: " + e.getMessage());
            redirectAttributes.addAttribute("pedidoId", pedidoId);
            return "redirect:/pdv";
        }
    }

    @PostMapping("/{pedidoId}/atribuir-mesa")
    public String atribuirMesa(
            @PathVariable Long pedidoId,
            @RequestParam Long mesaId,
            RedirectAttributes redirectAttributes) {
        try {
            pedidoService.atribuirMesa(pedidoId, mesaId);
            redirectAttributes.addAttribute("pedidoId", pedidoId);
            return "redirect:/pdv";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atribuir mesa: " + e.getMessage());
            redirectAttributes.addAttribute("pedidoId", pedidoId);
            return "redirect:/pdv";
        }
    }

    @PostMapping("/{pedidoId}/desatribuir-mesa")
    public String desatribuirMesa(
            @PathVariable Long pedidoId,
            RedirectAttributes redirectAttributes) {
        try {
            pedidoService.desatribuirMesa(pedidoId);
            redirectAttributes.addAttribute("pedidoId", pedidoId);
            return "redirect:/pdv";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao remover mesa: " + e.getMessage());
            redirectAttributes.addAttribute("pedidoId", pedidoId);
            return "redirect:/pdv";
        }
    }

    @PostMapping("/{pedidoId}/adicionar-item")
    public String adicionarItem(
            @PathVariable Long pedidoId,
            @RequestParam Long produtoId,
            @RequestParam(defaultValue = "1") int quantidade,
            RedirectAttributes redirectAttributes) {
        try {
            pedidoService.adicionarItem(pedidoId, produtoId, quantidade);
            redirectAttributes.addAttribute("pedidoId", pedidoId);
            return "redirect:/pdv";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao adicionar item: " + e.getMessage());
            redirectAttributes.addAttribute("pedidoId", pedidoId);
            return "redirect:/pdv";
        }
    }

    @PostMapping("/{pedidoId}/remover-item/{itemId}")
    public String removerItem(
            @PathVariable Long pedidoId,
            @PathVariable Long itemId,
            RedirectAttributes redirectAttributes) {
        try {
            pedidoService.removerItem(pedidoId, itemId);
            redirectAttributes.addAttribute("pedidoId", pedidoId);
            return "redirect:/pdv";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao remover item: " + e.getMessage());
            redirectAttributes.addAttribute("pedidoId", pedidoId);
            return "redirect:/pdv";
        }
    }

    @PostMapping("/{pedidoId}/atualizar-quantidade")
    public String atualizarQuantidade(
            @PathVariable Long pedidoId,
            @RequestParam Long itemId,
            @RequestParam int quantidade,
            RedirectAttributes redirectAttributes) {
        try {
            pedidoService.atualizarQuantidadeItem(pedidoId, itemId, quantidade);
            redirectAttributes.addAttribute("pedidoId", pedidoId);
            return "redirect:/pdv";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar quantidade: " + e.getMessage());
            redirectAttributes.addAttribute("pedidoId", pedidoId);
            return "redirect:/pdv";
        }
    }

    @PostMapping("/{pedidoId}/desconto")
    public String aplicarDesconto(
            @PathVariable Long pedidoId,
            @RequestParam java.math.BigDecimal valor,
            RedirectAttributes redirectAttributes) {
        try {
            pedidoService.aplicarDesconto(pedidoId, valor);
            redirectAttributes.addFlashAttribute("sucesso", "Desconto aplicado com sucesso.");
            redirectAttributes.addAttribute("pedidoId", pedidoId);
            return "redirect:/pdv";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao aplicar desconto: " + e.getMessage());
            redirectAttributes.addAttribute("pedidoId", pedidoId);
            return "redirect:/pdv";
        }
    }

    @PostMapping("/{pedidoId}/fechar")
    public String fecharPedido(
            @PathVariable Long pedidoId,
            @RequestParam TipoPagamento formaPagamento,
            RedirectAttributes redirectAttributes) {
        try {
            pedidoService.fecharPedido(pedidoId, formaPagamento);
            redirectAttributes.addFlashAttribute("sucesso", "Pedido fechado com sucesso.");
            return "redirect:/pdv/nota/" + pedidoId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao fechar pedido: " + e.getMessage());
            redirectAttributes.addAttribute("pedidoId", pedidoId);
            return "redirect:/pdv";
        }
    }

    @PostMapping("/{pedidoId}/cancelar")
    public String cancelarPedido(
            @PathVariable Long pedidoId,
            RedirectAttributes redirectAttributes) {
        try {
            pedidoService.cancelarPedido(pedidoId);
            redirectAttributes.addFlashAttribute("sucesso", "Pedido cancelado com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao cancelar pedido: " + e.getMessage());
        }
        return "redirect:/pdv";
    }

    @PostMapping("/{pedidoId}/dividir")
    public String dividirConta(
            @PathVariable Long pedidoId,
            @RequestParam int numPessoas,
            RedirectAttributes redirectAttributes) {
        try {
            pedidoService.dividirConta(pedidoId, numPessoas);
            redirectAttributes.addFlashAttribute("sucesso", "Conta dividida com sucesso.");
            redirectAttributes.addAttribute("pedidoId", pedidoId);
            return "redirect:/pdv";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao dividir conta: " + e.getMessage());
            redirectAttributes.addAttribute("pedidoId", pedidoId);
            return "redirect:/pdv";
        }
    }

    @PostMapping("/divisao/{divisaoId}/pagar")
    public String pagarDivisao(
            @PathVariable Long divisaoId,
            RedirectAttributes redirectAttributes) {
        try {
            pedidoService.pagamentoDividido(divisaoId);
            redirectAttributes.addFlashAttribute("sucesso", "Parcela paga com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao pagar parcela: " + e.getMessage());
        }
        return "redirect:/pdv";
    }

    @GetMapping("/nota/{pedidoId}")
    public String nota(@PathVariable Long pedidoId, Model model, RedirectAttributes redirectAttributes) {
        return pedidoService.buscarPorId(pedidoId)
                .map(pedido -> {
                    model.addAttribute("pedido", pedido);
                    return "nota";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("erro", "Pedido não encontrado.");
                    return "redirect:/pdv";
                });
    }

    @GetMapping("/api/pedidos-abertos")
    @ResponseBody
    public ResponseEntity<List<Pedido>> pedidosAbertos() {
        return ResponseEntity.ok(pedidoService.pedidosAbertos());
    }
}
