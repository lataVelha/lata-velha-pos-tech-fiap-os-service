package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public interface CriarOrdemServicoGateway {
    Proprietario getProprietarioAtivoPorId(Long id);
    Veiculo getVeiculoAtivoDoProprietario(Long veiculoId, Long proprietarioId);
    Funcionario getFuncionarioPorUserId(UserId userId);
    OrdemServico salvarOrdemServico(OrdemServico os);
    OrdemServicoProjection getOrdemServicoProjectionById(Long id);
}
