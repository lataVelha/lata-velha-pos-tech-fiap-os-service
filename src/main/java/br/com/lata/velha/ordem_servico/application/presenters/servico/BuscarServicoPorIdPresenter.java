package br.com.lata.velha.ordem_servico.application.presenters.servico;

import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;

public interface BuscarServicoPorIdPresenter {
    ServicoResponse present(Servico servico);
}
