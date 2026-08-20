package com.pdv.controller;

import com.pdv.model.Cliente;
import com.pdv.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        return "clientes";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente-form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return clienteService.buscarPorId(id)
                .map(cliente -> {
                    model.addAttribute("cliente", cliente);
                    return "cliente-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado.");
                    return "redirect:/clientes";
                });
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Cliente cliente, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cliente-form";
        }
        try {
            clienteService.salvar(cliente);
            redirectAttributes.addFlashAttribute("sucesso", "Cliente salvo com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar cliente: " + e.getMessage());
        }
        return "redirect:/clientes";
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.deletar(id);
            redirectAttributes.addFlashAttribute("sucesso", "Cliente removido com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao remover cliente: " + e.getMessage());
        }
        return "redirect:/clientes";
    }

    @GetMapping("/api/buscar")
    @ResponseBody
    public ResponseEntity<List<Cliente>> buscar(@RequestParam(name = "q", required = false) String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(clienteService.listarTodos());
        }
        List<Cliente> resultados = clienteService.buscarPorNome(query);
        return ResponseEntity.ok(resultados);
    }
}
