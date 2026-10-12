package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;

public interface AtualizarVeiculoGateway {
    Veiculo getVeiculoPorId(Long id);
    Proprietario getProprietarioAtivoPorId(Long id);
    Veiculo salvarVeiculo(Veiculo v);
}
