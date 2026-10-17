package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;

import java.util.List;

public interface ListarVeiculosPorProprietarioGateway {
    List<Veiculo> findByProprietarioId(Long proprietarioId);
}
