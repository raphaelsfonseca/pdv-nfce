package com.pdv.config;

import com.pdv.enums.StatusMesa;
import com.pdv.model.Cliente;
import com.pdv.model.Mesa;
import com.pdv.repository.ClienteRepository;
import com.pdv.repository.MesaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MesaRepository mesaRepository;
    private final ClienteRepository clienteRepository;

    public DataInitializer(MesaRepository mesaRepository, ClienteRepository clienteRepository) {
        this.mesaRepository = mesaRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (mesaRepository.count() > 0) {
            return;
        }

        insertMesas();
        insertClientes();
    }

    private void insertMesas() {
        for (int i = 1; i <= 5; i++) {
            mesaRepository.save(Mesa.builder()
                    .numero(i)
                    .capacidade(4)
                    .status(StatusMesa.LIVRE)
                    .build());
        }
    }

    private void insertClientes() {
        clienteRepository.save(Cliente.builder()
                .nome("João Silva").cpfCnpj("12345678901")
                .telefone("(11) 99999-1111").email("joao.silva@email.com")
                .ativo(true).build());
        clienteRepository.save(Cliente.builder()
                .nome("Maria Santos").cpfCnpj("98765432100")
                .telefone("(11) 99999-2222").email("maria.santos@email.com")
                .ativo(true).build());
        clienteRepository.save(Cliente.builder()
                .nome("Pedro Oliveira").cpfCnpj("45678912300")
                .telefone("(11) 99999-3333").email("pedro.oliveira@email.com")
                .ativo(true).build());
    }
}
