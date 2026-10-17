package br.com.lata.velha.ordem_servico.api.presenters.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.application.presenters.veiculo.AtualizarVeiculoPresenter;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import org.springframework.stereotype.Component;

@Component
public class AtualizarVeiculoPresenterImpl implements AtualizarVeiculoPresenter {
    @Override
    public VeiculoResponse present(Veiculo veiculo) {
        return VeiculoResponse.from(veiculo);
    }
}
