package br.com.lata.velha.ordem_servico.api.presenters.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.application.presenters.veiculo.CriarVeiculoPresenter;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import org.springframework.stereotype.Component;

@Component
public class CriarVeiculoPresenterImpl implements CriarVeiculoPresenter {
    @Override
    public VeiculoResponse present(Veiculo veiculo) {
        return VeiculoResponse.from(veiculo);
    }
}
