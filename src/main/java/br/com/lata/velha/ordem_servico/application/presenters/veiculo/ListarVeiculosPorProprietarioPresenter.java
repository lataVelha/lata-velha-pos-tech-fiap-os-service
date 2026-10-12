package br.com.lata.velha.ordem_servico.application.presenters.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;

import java.util.List;

public interface ListarVeiculosPorProprietarioPresenter {
    List<VeiculoResponse> present(List<Veiculo> veiculos);
}
