package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.shared.application.gateways.EmailProvider;
import br.com.lata.velha.shared.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.shared.application.logging.Logger;

import java.util.Map;

public class NotificarCadastroProprietarioUseCase {

    private final EmailProvider emailProvider;
    private final EmailTemplateProvider templateProvider;
    private final Logger logger;

    public NotificarCadastroProprietarioUseCase(EmailProvider emailProvider, EmailTemplateProvider templateProvider, Logger logger) {
        this.emailProvider = emailProvider;
        this.templateProvider = templateProvider;
        this.logger = logger;
    }

    public void execute(Proprietario proprietario) {
        try {
            Map<String, Object> variables = Map.of(
                    "nome", proprietario.getNome(),
                    "documento", proprietario.getDocumento().getMasked()
            );

            String html = templateProvider.render("proprietario-cadastro", variables);
            emailProvider.send(proprietario.getEmail(), "Bem-vindo ao Lata Velha!", html);
            logger.logInfo("E-mail de boas-vindas enviado - proprietarioId={}", proprietario.getId());

        } catch (Exception e) {
            logger.logError("Falha ao enviar e-mail de cadastro de proprietário - proprietarioId=" + proprietario.getId(), e);
        }
    }
}
