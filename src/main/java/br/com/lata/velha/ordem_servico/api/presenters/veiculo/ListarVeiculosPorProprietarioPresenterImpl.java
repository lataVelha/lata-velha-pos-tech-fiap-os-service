package br.com.lata.velha.ordem_servico.api.presenters.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.application.presenters.veiculo.ListarVeiculosPorProprietarioPresenter;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListarVeiculosPorProprietarioPresenterImpl implements ListarVeiculosPorProprietarioPresenter {
    @Override
    public List<VeiculoResponse> present(List<Veiculo> veiculos) {
        return veiculos.stream().map(VeiculoResponse::from).toList();
    }
}
