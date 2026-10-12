package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.shared.application.logging.Logger;

public class DesativarProprietarioUseCase {

    private final DesativarProprietarioGateway gateway;
    private final Logger logger;

    public DesativarProprietarioUseCase(DesativarProprietarioGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public void execute(Long id) {
        logger.logInfo("Buscando proprietário para desativação - proprietarioId={}", id);
        var proprietario = gateway.getProprietarioPorId(id);
        proprietario.deactivate();

        logger.logInfo("Salvando desativação do proprietário - proprietarioId={}", id);
        gateway.salvarProprietario(proprietario);
        logger.logInfo("Proprietário desativado com sucesso - proprietarioId={}", id);
    }
}
