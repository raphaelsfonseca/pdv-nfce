package com.pdv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    private String cpfCnpj;
    private String telefone;
    private String email;
    private String endereco;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro = LocalDateTime.now();

    public Cliente() {}

    public Cliente(Long id, String nome, String cpfCnpj, String telefone, String email,
                   String endereco, boolean ativo, LocalDateTime dataCadastro) {
        this.id = id;
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.ativo = ativo;
        this.dataCadastro = dataCadastro;
    }

    public static ClienteBuilder builder() {
        return new ClienteBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public static class ClienteBuilder {
        private Long id;
        private String nome;
        private String cpfCnpj;
        private String telefone;
        private String email;
        private String endereco;
        private boolean ativo = true;
        private LocalDateTime dataCadastro = LocalDateTime.now();

        ClienteBuilder() {}

        public ClienteBuilder id(Long id) { this.id = id; return this; }
        public ClienteBuilder nome(String nome) { this.nome = nome; return this; }
        public ClienteBuilder cpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; return this; }
        public ClienteBuilder telefone(String telefone) { this.telefone = telefone; return this; }
        public ClienteBuilder email(String email) { this.email = email; return this; }
        public ClienteBuilder endereco(String endereco) { this.endereco = endereco; return this; }
        public ClienteBuilder ativo(boolean ativo) { this.ativo = ativo; return this; }
        public ClienteBuilder dataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; return this; }

        public Cliente build() {
            return new Cliente(id, nome, cpfCnpj, telefone, email, endereco, ativo, dataCadastro);
        }
    }
}
