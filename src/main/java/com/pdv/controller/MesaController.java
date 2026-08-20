package com.pdv.controller;

import com.pdv.enums.StatusMesa;
import com.pdv.model.Mesa;
import com.pdv.service.ClienteService;
import com.pdv.service.MesaService;
import com.pdv.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/mesas")
public class MesaController {

    private final MesaService mesaService;
    private final PedidoService pedidoService;
    private final ClienteService clienteService;

    public MesaController(MesaService mesaService, PedidoService pedidoService, ClienteService clienteService) {
        this.mesaService = mesaService;
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("mesas", mesaService.listarTodas());
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("totalMesas", mesaService.listarTodas().size());
        model.addAttribute("mesasOcupadas", mesaService.contarPorStatus(StatusMesa.OCUPADA));
        model.addAttribute("mesasLivres", mesaService.contarPorStatus(StatusMesa.LIVRE));
        model.addAttribute("mesasReservadas", mesaService.contarPorStatus(StatusMesa.RESERVADA));
        return "mesas";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("mesa", new Mesa());
        return "mesa-form";
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Mesa> buscarPorId(@PathVariable Long id) {
        return mesaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/{id}/abrir")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> abrir(@PathVariable Long id) {
        try {
            mesaService.abrirMesa(id);
            Mesa mesa = mesaService.buscarPorId(id).orElseThrow();
            return ResponseEntity.ok(Map.of("sucesso", true, "mensagem", "Mesa aberta com sucesso.", "mesa", mesa));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("sucesso", false, "mensagem", e.getMessage()));
        }
    }

    @PostMapping("/api/{id}/fechar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> fechar(@PathVariable Long id) {
        try {
            mesaService.fecharMesa(id);
            Mesa mesa = mesaService.buscarPorId(id).orElseThrow();
            return ResponseEntity.ok(Map.of("sucesso", true, "mensagem", "Mesa fechada com sucesso.", "mesa", mesa));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("sucesso", false, "mensagem", e.getMessage()));
        }
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Mesa mesa, RedirectAttributes redirectAttributes) {
        try {
            mesaService.salvar(mesa);
            redirectAttributes.addFlashAttribute("sucesso", "Mesa criada com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar mesa: " + e.getMessage());
        }
        return "redirect:/mesas";
    }
}
