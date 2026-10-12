package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.shared.domain.value_objects.UserId;

import java.util.List;
import java.util.Set;

public interface AprovarOrdemServicoGateway {
    OrdemServico getOrdemServicoComServicosEPecas(Long id);
    Funcionario getFuncionarioPorUserId(UserId userId);
    List<Servico> getServicosAtivosPorIds(Set<Long> ids);
    OrdemServico salvarOrdemServico(OrdemServico os);
}
