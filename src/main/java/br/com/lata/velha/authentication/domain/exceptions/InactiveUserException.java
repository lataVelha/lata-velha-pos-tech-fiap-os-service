package br.com.lata.velha.authentication.domain.exceptions;

import br.com.lata.velha.shared.domain.exceptions.DomainException;

public class InactiveUserException extends DomainException {
    private InactiveUserException(String message) {
        super(message);
    }

    public static InactiveUserException fromEntityName(String entityName) {
        var message = composeMessage(entityName);
        return new InactiveUserException(message);
    }

    private static String composeMessage(String className) {
        return className + "está inativo!";
    }
}
