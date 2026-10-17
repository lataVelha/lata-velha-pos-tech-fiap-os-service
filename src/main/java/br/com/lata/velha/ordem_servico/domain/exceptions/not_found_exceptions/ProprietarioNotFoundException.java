package br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.shared.domain.exceptions.NotFoundException;

public class ProprietarioNotFoundException extends NotFoundException {
    private ProprietarioNotFoundException(String param, String value) {
        super(Proprietario.class, param, value);
    }

    public static ProprietarioNotFoundException fromId(Long value) {
        return new ProprietarioNotFoundException("id", value.toString());
    }

    public static ProprietarioNotFoundException fromDocumento(String documento) {
        return new ProprietarioNotFoundException("documento", documento);
    }
}