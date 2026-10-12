package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class BuscarOrdemServicoUseCase {

    private final BuscarOrdemServicoGateway gateway;
    private final Logger logger;

    public BuscarOrdemServicoUseCase(BuscarOrdemServicoGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public PaginatedResult<OrdemServicoProjection> execute(Long id,
                                                           StatusOrdemServico status,
                                                           Long proprietarioId,
                                                           Long mecanicoId,
                                                           int page,
                                                           int size) {
        logger.logInfo("Buscando ordens de serviço por filtros - id={}, status={}, proprietarioId={}, mecanicoId={}, page={}, size={}",
                id, status, proprietarioId, mecanicoId, page, size);
        return gateway.findByFiltros(
                id,
                status != null ? status.name() : null,
                proprietarioId,
                mecanicoId,
                page,
                size
        );
    }
}
