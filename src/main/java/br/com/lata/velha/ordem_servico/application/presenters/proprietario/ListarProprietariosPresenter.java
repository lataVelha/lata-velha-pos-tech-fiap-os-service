package br.com.lata.velha.ordem_servico.application.presenters.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public interface ListarProprietariosPresenter {
    PaginatedResult<ProprietarioResponse> present(PaginatedResult<Proprietario> proprietarios);
}
