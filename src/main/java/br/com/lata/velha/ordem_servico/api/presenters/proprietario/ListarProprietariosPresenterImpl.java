package br.com.lata.velha.ordem_servico.api.presenters.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordem_servico.application.presenters.proprietario.ListarProprietariosPresenter;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import org.springframework.stereotype.Component;

@Component
public class ListarProprietariosPresenterImpl implements ListarProprietariosPresenter {
    @Override
    public PaginatedResult<ProprietarioResponse> present(PaginatedResult<Proprietario> proprietarios) {
        return PaginatedResult.map(proprietarios, ProprietarioResponse::from);
    }
}
