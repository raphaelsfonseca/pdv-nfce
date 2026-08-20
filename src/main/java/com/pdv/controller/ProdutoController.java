package com.pdv.controller;

import com.pdv.model.Produto;
import com.pdv.service.ProdutoService;
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
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", produtoService.listarTodos());
        model.addAttribute("categorias", produtoService.listarCategorias());
        return "produtos";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("produto", new Produto());
        return "produto-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return produtoService.buscarPorId(id)
                .map(produto -> {
                    model.addAttribute("produto", produto);
                    return "produto-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("erro", "Produto não encontrado.");
                    return "redirect:/produtos";
                });
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Produto produto, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "produto-form";
        }
        try {
            produtoService.salvar(produto);
            redirectAttributes.addFlashAttribute("sucesso", "Produto salvo com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar produto: " + e.getMessage());
        }
        return "redirect:/produtos";
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produtoService.deletar(id);
            redirectAttributes.addFlashAttribute("sucesso", "Produto removido com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao remover produto: " + e.getMessage());
        }
        return "redirect:/produtos";
    }

    @GetMapping("/api/buscar")
    @ResponseBody
    public ResponseEntity<List<Produto>> buscar(@RequestParam(name = "q", required = false) String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(produtoService.listarAtivos());
        }
        List<Produto> resultados = produtoService.buscarPorNome(query);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Produto> buscarPorIdApi(@PathVariable Long id) {
        return produtoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/estoque-baixo")
    public String estoqueBaixo(Model model) {
        model.addAttribute("produtos", produtoService.produtosEstoqueBaixo());
        model.addAttribute("categorias", produtoService.listarCategorias());
        model.addAttribute("filtroEstoqueBaixo", true);
        return "produtos";
    }
}
