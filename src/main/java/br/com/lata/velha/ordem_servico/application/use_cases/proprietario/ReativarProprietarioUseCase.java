package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.shared.application.logging.Logger;

public class ReativarProprietarioUseCase {

    private final ReativarProprietarioGateway gateway;
    private final Logger logger;

    public ReativarProprietarioUseCase(ReativarProprietarioGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public Proprietario execute(Long id) {
        logger.logInfo("Buscando proprietário inativo para reativação - proprietarioId={}", id);
        Proprietario proprietario = gateway.getProprietarioInativoPorId(id);
        proprietario.activate();

        logger.logInfo("Salvando reativação do proprietário - proprietarioId={}", id);
        Proprietario reativado = gateway.salvarProprietario(proprietario);
        logger.logInfo("Proprietário reativado com sucesso - proprietarioId={}", id);
        return reativado;
    }
}
