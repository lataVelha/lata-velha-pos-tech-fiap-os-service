package br.com.lata.velha.ordem_servico.application.services.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;

import java.util.List;
import java.util.Set;

public interface NotificarOrdemServicoGateway {
    Proprietario getProprietarioPorId(Long id);
    Veiculo getVeiculoPorId(Long id);
    List<Servico> getServicosAtivosPorIds(Set<Long> ids);
    Servico getServicoAtivoPorId(Long id);
}
