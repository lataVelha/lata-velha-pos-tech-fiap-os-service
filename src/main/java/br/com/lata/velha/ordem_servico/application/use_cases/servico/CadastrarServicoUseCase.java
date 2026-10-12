package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarServicoRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.shared.application.logging.Logger;

public class CadastrarServicoUseCase {

    private final CadastrarServicoGateway gateway;
    private final Logger logger;

    public CadastrarServicoUseCase(CadastrarServicoGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public Servico execute(CadastrarServicoRequest request) {
        logger.logInfo("Cadastrando serviço - nome={}", request.nome());
        var saved = gateway.salvarServico(request.toDomain());
        logger.logInfo("Serviço cadastrado com sucesso - servicoId={}", saved.getId());
        return saved;
    }
}
