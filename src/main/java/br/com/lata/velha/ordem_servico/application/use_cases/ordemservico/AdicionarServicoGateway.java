package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;

import java.util.List;
import java.util.Set;

public interface AdicionarServicoGateway {
    OrdemServico getOrdemServicoPorId(Long id);
    List<Servico> getServicosAtivosPorIds(Set<Long> ids);
    OrdemServico salvarOrdemServico(OrdemServico os);
}
