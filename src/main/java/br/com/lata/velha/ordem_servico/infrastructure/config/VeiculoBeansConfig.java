package br.com.lata.velha.ordem_servico.infrastructure.config;

import br.com.lata.velha.ordem_servico.application.controllers.veiculo.VeiculoCleanController;
import br.com.lata.velha.ordem_servico.application.presenters.veiculo.*;
import br.com.lata.velha.ordem_servico.application.use_cases.veiculo.*;
import br.com.lata.velha.shared.application.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class VeiculoBeansConfig {

    private final Logger logger;
    private final CriarVeiculoGateway criarGateway;
    private final CriarVeiculoPresenter criarPresenter;
    private final AtualizarVeiculoGateway atualizarGateway;
    private final AtualizarVeiculoPresenter atualizarPresenter;
    private final BuscarVeiculoPorIdGateway buscarPorIdGateway;
    private final BuscarVeiculoPorIdPresenter buscarPorIdPresenter;
    private final ListarVeiculosGateway listarGateway;
    private final ListarVeiculosPresenter listarPresenter;
    private final ListarVeiculosPorProprietarioGateway listarPorProprietarioGateway;
    private final ListarVeiculosPorProprietarioPresenter listarPorProprietarioPresenter;
    private final DesativarVeiculoGateway desativarGateway;
    private final ReativarVeiculoGateway reativarGateway;
    private final ReativarVeiculoPresenter reativarPresenter;

    @Bean
    public VeiculoCleanController veiculoCleanController() {
        logger.logInfo("Configurando VeiculoCleanController");
        return new VeiculoCleanController(
                criarGateway,
                criarPresenter,
                atualizarGateway,
                atualizarPresenter,
                buscarPorIdGateway,
                buscarPorIdPresenter,
                listarGateway,
                listarPresenter,
                listarPorProprietarioGateway,
                listarPorProprietarioPresenter,
                desativarGateway,
                reativarGateway,
                reativarPresenter,
                logger);
    }
}
