package com.pdv.service;

import com.pdv.enums.StatusMesa;
import com.pdv.model.Mesa;
import com.pdv.repository.MesaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MesaService {

    private final MesaRepository mesaRepository;

    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    public List<Mesa> listarTodas() {
        return mesaRepository.findAll();
    }

    public Optional<Mesa> buscarPorId(Long id) {
        return mesaRepository.findById(id);
    }

    public Optional<Mesa> buscarPorNumero(Integer numero) {
        return mesaRepository.findByNumero(numero);
    }

    public List<Mesa> buscarPorStatus(StatusMesa status) {
        return mesaRepository.findByStatus(status);
    }

    public Mesa salvar(Mesa mesa) {
        return mesaRepository.save(mesa);
    }

    public Mesa abrirMesa(Long mesaId) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RuntimeException("Mesa não encontrada com id: " + mesaId));
        mesa.setStatus(StatusMesa.OCUPADA);
        return mesaRepository.save(mesa);
    }

    public Mesa fecharMesa(Long mesaId) {
        Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new RuntimeException("Mesa não encontrada com id: " + mesaId));
        mesa.setStatus(StatusMesa.LIVRE);
        mesa.setPedidoAtual(null);
        return mesaRepository.save(mesa);
    }

    public long contarPorStatus(StatusMesa status) {
        return mesaRepository.countByStatus(status);
    }
}
