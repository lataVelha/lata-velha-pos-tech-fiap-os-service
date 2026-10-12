package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public interface ListarProprietariosGateway {
    PaginatedResult<Proprietario> findAll(int page, int size);
}
