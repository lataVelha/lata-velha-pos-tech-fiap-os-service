package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;

public interface BuscarProprietarioPorDocumentoGateway {
    Proprietario getProprietarioPorDocumento(String documento);
}
