package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;

public interface ReativarVeiculoGateway {
    Veiculo getVeiculoInativoPorId(Long id);
    Veiculo salvarVeiculo(Veiculo v);
}
