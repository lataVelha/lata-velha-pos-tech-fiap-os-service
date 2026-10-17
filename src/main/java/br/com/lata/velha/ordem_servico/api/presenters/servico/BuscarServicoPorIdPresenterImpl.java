package br.com.lata.velha.ordem_servico.api.presenters.servico;

import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordem_servico.application.presenters.servico.BuscarServicoPorIdPresenter;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import org.springframework.stereotype.Component;

@Component
public class BuscarServicoPorIdPresenterImpl implements BuscarServicoPorIdPresenter {
    @Override
    public ServicoResponse present(Servico servico) {
        return ServicoResponse.from(servico);
    }
}
