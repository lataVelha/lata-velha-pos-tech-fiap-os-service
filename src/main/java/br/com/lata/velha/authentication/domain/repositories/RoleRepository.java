package br.com.lata.velha.authentication.domain.repositories;

import br.com.lata.velha.authentication.domain.entities.Role;

import java.util.Set;

public interface RoleRepository {
    Role getByNome(String nome);
    Set<Role> getByNomes(Set<String> names);
}
