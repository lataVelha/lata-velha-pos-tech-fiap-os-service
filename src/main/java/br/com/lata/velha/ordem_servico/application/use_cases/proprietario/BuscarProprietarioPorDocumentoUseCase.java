package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.shared.application.logging.Logger;

public class BuscarProprietarioPorDocumentoUseCase {

    private final BuscarProprietarioPorDocumentoGateway gateway;
    private final Logger logger;

    public BuscarProprietarioPorDocumentoUseCase(BuscarProprietarioPorDocumentoGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public Proprietario execute(String documento) {
        logger.logInfo("Buscando proprietário por documento");
        String cleaned = documento.replaceAll("[^\\dA-Za-z]", "").toUpperCase();
        return gateway.getProprietarioPorDocumento(cleaned);
    }
}
