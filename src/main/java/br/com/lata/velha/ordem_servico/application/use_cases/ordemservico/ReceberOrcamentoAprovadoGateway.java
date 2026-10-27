package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;

public interface ReceberOrcamentoAprovadoGateway {
    OrdemServico getOrdemServicoComServicosEPecas(Long id);
    OrdemServico salvarOrdemServico(OrdemServico os);
}
