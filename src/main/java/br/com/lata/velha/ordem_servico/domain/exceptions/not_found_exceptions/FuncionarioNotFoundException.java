package br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions;

import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.shared.domain.exceptions.NotFoundException;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class FuncionarioNotFoundException extends NotFoundException {
    private FuncionarioNotFoundException(String paramName, String paramValue) {
        super(Funcionario.class, paramName, paramValue);
    }

    public static FuncionarioNotFoundException fromId(Long id) {
        return new FuncionarioNotFoundException("id", id.toString());
    }

    public static FuncionarioNotFoundException fromUserId(UserId userId) {
        return new FuncionarioNotFoundException("userId", userId.toString());
    }
}
