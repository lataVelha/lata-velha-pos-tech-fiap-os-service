package br.com.lata.velha.ordem_servico.application.presenters.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;

public interface CriarOrdemServicoPresenter {
    OrdemServicoResponse present(OrdemServicoProjection projection);
}
