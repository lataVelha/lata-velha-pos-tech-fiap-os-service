package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public interface FinalizarServicoGateway {
    OrdemServico getOrdemServicoComServicosEPecas(Long id);
    Funcionario getFuncionarioPorUserId(UserId userId);
    OrdemServico salvarOrdemServico(OrdemServico os);
}
