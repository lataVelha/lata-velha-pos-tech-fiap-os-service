package br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.shared.domain.exceptions.NotFoundException;

public class ExecucaoServicoNotFoundException extends NotFoundException {
    private ExecucaoServicoNotFoundException(String param, String value) {
        super(ExecucaoServico.class, param, value);
    }

    public static ExecucaoServicoNotFoundException fromId(Long value) {
        return new ExecucaoServicoNotFoundException("id", value.toString());
    }
}
