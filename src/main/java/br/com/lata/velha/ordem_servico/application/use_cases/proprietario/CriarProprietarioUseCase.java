package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.shared.application.logging.Logger;

public class CriarProprietarioUseCase {

    private final CriarProprietarioGateway gateway;
    private final NotificarCadastroProprietarioUseCase notificarUseCase;
    private final Logger logger;

    public CriarProprietarioUseCase(CriarProprietarioGateway gateway, NotificarCadastroProprietarioUseCase notificarUseCase, Logger logger) {
        this.gateway = gateway;
        this.notificarUseCase = notificarUseCase;
        this.logger = logger;
    }

    public Proprietario execute(ProprietarioRequest request) {
        logger.logInfo("Salvando novo proprietário");
        Proprietario saved = gateway.salvarProprietario(request.toDomain());
        logger.logInfo("Proprietário criado com sucesso - proprietarioId={}", saved.getId());

        logger.logInfo("Enviando notificação de cadastro - proprietarioId={}", saved.getId());
        notificarUseCase.execute(saved);
        return saved;
    }
}
