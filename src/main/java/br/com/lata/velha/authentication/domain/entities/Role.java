package br.com.lata.velha.authentication.domain.entities;

import br.com.lata.velha.shared.domain.value_objects.RoleId;

import java.util.Objects;

public final class Role {
    private final RoleId roleId;
    private String nome;

    public Role(RoleId roleId, String nome) {
        this.roleId = roleId;
        changeNome(nome);
    }

    public static Role create(String nome) {
        return new Role(RoleId.random(), nome);
    }

    public void changeNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da role não pode ser vazio");
        }
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Role{roleId=" + roleId + ", nome='" + nome + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(roleId, role.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId);
    }

    //region GETTERS
    public RoleId getRoleId() {
        return roleId;
    }

    public String getNome() {
        return nome;
    }
    //endregion
}