package br.com.lata.velha.authentication.domain.exceptions.not_found_exceptions;

import br.com.lata.velha.authentication.domain.entities.Role;
import br.com.lata.velha.shared.domain.exceptions.NotFoundException;

public class RoleNotFoundException extends NotFoundException {
    private RoleNotFoundException(String paramName, String paramValue) {
        super(Role.class, paramName, paramValue);
    }

    public static RoleNotFoundException fromNome(String nome) {
        return new RoleNotFoundException("nome", nome);
    }
}
