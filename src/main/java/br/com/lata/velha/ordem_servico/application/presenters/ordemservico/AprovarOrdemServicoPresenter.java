package br.com.lata.velha.ordem_servico.application.presenters.ordemservico;

import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.AprovarOrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;

public interface AprovarOrdemServicoPresenter {
    AprovarOrdemServicoResponse present(OrdemServico ordemServico);
}
