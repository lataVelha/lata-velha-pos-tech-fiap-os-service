package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.authentication.domain.entities.Role;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Cargo {
    private Long id;
    private String nome;
    private Set<Role> roles;

    public Cargo() {
        this.roles = new HashSet<>();
    }

    public Cargo(Long id, String nome, Set<Role> roles) {
        this.id = id;
        setNome(nome);
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
    }

    // --- business rules ---

    public void addRole(Role role) {
        Objects.requireNonNull(role, "Role não pode ser nula");
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(r -> r.getNome().equalsIgnoreCase(roleName));
    }

    // --- getters and setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome do cargo não pode ser vazio");
        this.nome = nome;
    }

    public Set<Role> getRoles() { return Collections.unmodifiableSet(roles); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cargo cargo = (Cargo) o;
        return Objects.equals(id, cargo.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Cargo{id=" + id + ", nome='" + nome + "', roles=" + roles.size() + "}";
    }
}