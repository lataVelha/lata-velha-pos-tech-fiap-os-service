package br.com.lata.velha.ordem_servico.application.presenters.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;

public interface AtualizarVeiculoPresenter {
    VeiculoResponse present(Veiculo veiculo);
}
