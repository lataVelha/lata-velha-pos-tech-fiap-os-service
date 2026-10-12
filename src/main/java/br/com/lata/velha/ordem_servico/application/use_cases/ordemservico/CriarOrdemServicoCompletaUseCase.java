package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoSemProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.CriarProprietarioGateway;
import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.CriarProprietarioUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.NotificarCadastroProprietarioUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.veiculo.CriarVeiculoGateway;
import br.com.lata.velha.ordem_servico.application.use_cases.veiculo.CriarVeiculoUseCase;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;

import java.util.List;

public class CriarOrdemServicoCompletaUseCase {

    private final CriarOrdemServicoGateway criarOrdemServicoGateway;
    private final CriarProprietarioGateway criarProprietarioGateway;
    private final CriarVeiculoGateway criarVeiculoGateway;
    private final AdicionarServicoGateway adicionarServicoGateway;
    private final NotificarOrdemServicoService notificarService;
    private final NotificarCadastroProprietarioUseCase notificarCadastroProprietarioUseCase;
    private final Logger logger;

    public CriarOrdemServicoCompletaUseCase(CriarOrdemServicoGateway criarOrdemServicoGateway,
                                             CriarProprietarioGateway criarProprietarioGateway,
                                             CriarVeiculoGateway criarVeiculoGateway,
                                             AdicionarServicoGateway adicionarServicoGateway,
                                             NotificarOrdemServicoService notificarService,
                                             NotificarCadastroProprietarioUseCase notificarCadastroProprietarioUseCase,
                                             Logger logger) {
        this.criarOrdemServicoGateway = criarOrdemServicoGateway;
        this.criarProprietarioGateway = criarProprietarioGateway;
        this.criarVeiculoGateway = criarVeiculoGateway;
        this.adicionarServicoGateway = adicionarServicoGateway;
        this.notificarService = notificarService;
        this.notificarCadastroProprietarioUseCase = notificarCadastroProprietarioUseCase;
        this.logger = logger;
    }

    public OrdemServicoProjection execute(Input input) {
        var proprietario = new CriarProprietarioUseCase(criarProprietarioGateway, notificarCadastroProprietarioUseCase, logger)
                .execute(input.proprietario());

        var veiculo = new CriarVeiculoUseCase(criarVeiculoGateway, logger)
                .execute(input.veiculo().toVeiculoRequest(proprietario.getId()));

        var funcionario = criarOrdemServicoGateway.getFuncionarioPorUserId(input.userId());

        var ordemServico = OrdemServico.create(
                proprietario.getId(),
                veiculo.getId(),
                input.reclamacaoProprietario(),
                funcionario.getId()
        );
        var saved = criarOrdemServicoGateway.salvarOrdemServico(ordemServico);

        if (input.servicos() != null && !input.servicos().isEmpty()) {
            new AdicionarServicoUseCase(adicionarServicoGateway, logger)
                    .execute(new AdicionarServicoUseCase.Input(saved.getId(), input.servicos()));
        }

        notificarService.execute(saved);

        return criarOrdemServicoGateway.getOrdemServicoProjectionById(saved.getId());
    }

    public record Input(ProprietarioRequest proprietario, VeiculoSemProprietarioRequest veiculo, UserId userId,
                        String reclamacaoProprietario,
                        List<AdicionarServicoUseCase.Input.ServicoAdicionar> servicos) {}
}
