package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.shared.application.logging.Logger;

public class AtualizarProprietarioUseCase {

    private final AtualizarProprietarioGateway gateway;
    private final Logger logger;

    public AtualizarProprietarioUseCase(AtualizarProprietarioGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public Proprietario execute(Long id, ProprietarioRequest request) {
        logger.logInfo("Buscando proprietário para atualização - proprietarioId={}", id);
        Proprietario existing = gateway.getProprietarioPorId(id);
        request.updateDomain(existing);

        logger.logInfo("Salvando proprietário atualizado - proprietarioId={}", id);
        Proprietario updated = gateway.salvarProprietario(existing);
        logger.logInfo("Proprietário atualizado com sucesso - proprietarioId={}", id);
        return updated;
    }
}
