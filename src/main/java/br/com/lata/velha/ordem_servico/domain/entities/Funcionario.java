package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.shared.domain.value_objects.UserId;

import java.util.Objects;

public final class Funcionario {
    private Long id;
    private String nome;
    private Cargo cargo;
    private final UserId userId;

    public Funcionario(Long id, String nome, Cargo cargo, UserId userId) {
        this.id = id;
        this.nome = nome;
        this.cargo = cargo;
        this.userId = userId;
    }

    public static Funcionario create(String nome, Cargo cargo, UserId userId) {
        return new Funcionario(null, nome, cargo, userId);
    }

    public void update(String nome, Cargo cargo) {
        setNome(nome);
        setCargo(cargo);
    }

    private void setNome(String nome) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome do funcionário não pode ser vazio");
        this.nome = nome;
    }

    private void setCargo(Cargo cargo) {
        if(cargo == null)
            throw new IllegalArgumentException("Cargo do funcionário não pode ser nulo");
        this.cargo = cargo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Funcionario that = (Funcionario) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    //region GETTERS
    public Long getId() { return id; }

    public String getNome() { return nome; }

    public Cargo getCargo() { return cargo; }

    public UserId getUserId() {
        return userId;
    }
    //endregion
}