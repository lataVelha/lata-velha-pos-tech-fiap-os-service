package br.com.lata.velha.ordem_servico.api.presenters.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.presenters.ordemservico.CriarOrdemServicoPresenter;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import org.springframework.stereotype.Component;

@Component
public class CriarOrdemServicoPresenterImpl implements CriarOrdemServicoPresenter {
    @Override
    public OrdemServicoResponse present(OrdemServicoProjection projection) {
        return OrdemServicoResponse.from(projection);
    }
}
