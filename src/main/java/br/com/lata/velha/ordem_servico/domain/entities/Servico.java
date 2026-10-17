package br.com.lata.velha.ordem_servico.domain.entities;

import java.util.Objects;

public class Servico {

    private Long id;
    private String nome;
    private String descricao;
    private boolean ativo = true;

    public Servico() {
    }

    public Servico(Long id, String nome, String descricao) {
        this(id, nome, descricao, true);
    }

    public Servico(Long id, String nome, String descricao, boolean ativo) {
        this.id = id;
        atualizar(nome, descricao);
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do serviço não pode ser vazio");
        }
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição do serviço não pode ser vazia");
        }
        this.descricao = descricao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void atualizar(String nome, String descricao) {
        setNome(nome);
        setDescricao(descricao);
    }

    public void desativar() {
        if (!this.ativo) {
            throw new IllegalArgumentException("Serviço já está desativado");
        }
        this.ativo = false;
    }

    @Override
    public String toString() {
        return "Servico{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Servico)) return false;
        Servico servico = (Servico) o;
        return Objects.equals(id, servico.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setId(Long servicoId) {
        this.id = servicoId;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}