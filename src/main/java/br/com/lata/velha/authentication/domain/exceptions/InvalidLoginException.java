package br.com.lata.velha.authentication.domain.exceptions;


import br.com.lata.velha.shared.domain.exceptions.DomainException;

public  class InvalidLoginException extends DomainException {

    public InvalidLoginException() {
        this("Usuário ou senha inválidos");
    }

    public InvalidLoginException(String message) {
       super(message);
    }
}