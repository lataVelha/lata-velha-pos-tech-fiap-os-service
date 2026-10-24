package br.com.lata.velha.ordem_servico.application.controllers.ordemservico;

import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.AprovarOrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.TempoMedioExecucaoResponse;
import br.com.lata.velha.shared.application.gateways.EmailProvider;
import br.com.lata.velha.shared.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.application.presenters.ordemservico.*;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.*;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.*;
import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.CriarProprietarioGateway;
import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.NotificarCadastroProprietarioUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.veiculo.CriarVeiculoGateway;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.shared.application.gateways.EventPublisher;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.shared.domain.value_objects.UserId;

import java.time.LocalDate;

public class OrdemServicoCleanController {

    private final Logger logger;
    private final NotificarOrdemServicoService notificarService;
    private final EmailProvider emailProvider;
    private final EmailTemplateProvider templateProvider;

    private final CriarOrdemServicoGateway criarGateway;
    private final CriarOrdemServicoPresenter criarPresenter;
    private final CriarProprietarioGateway criarProprietarioGateway;
    private final CriarVeiculoGateway criarVeiculoGateway;
    private final AdicionarServicoGateway adicionarGateway;
    private final AprovarOrdemServicoGateway aprovarGateway;
    private final AprovarOrdemServicoPresenter aprovarPresenter;
    private final BuscarOrdemServicoGateway buscarGateway;
    private final BuscarOrdemServicoPresenter buscarPresenter;
    private final BuscarOrdensPorStatusOrdenadoGateway buscarOrdensPorStatusGateway;
    private final BuscarOrdensPorStatusPresenter buscarOrdensPorStatusPresenter;
    private final BuscarTempoMedioExecucaoGateway buscarTempoMedioGateway;
    private final BuscarTempoMedioExecucaoPresenter buscarTempoMedioPresenter;
    private final IniciarDiagnosticoGateway iniciarDiagnosticoGateway;
    private final FinalizarDiagnosticoGateway finalizarDiagnosticoGateway;
    private final ReprovarOrdemServicoGateway reprovarGateway;
    private final IniciarServicoGateway iniciarServicoGateway;
    private final FinalizarServicoGateway finalizarServicoGateway;
    private final RetirarVeiculoGateway retirarVeiculoGateway;
    private final ReceberAprovacaoOrcamentoClienteGateway receberAprovacaoGateway;
    private final ReceberAprovacaoOrcamentoClientePresenter receberAprovacaoPresenter;
    private final EventPublisher eventPublisher;

    public OrdemServicoCleanController(
            NotificarOrdemServicoGateway notificarGateway,
            EmailProvider emailProvider,
            EmailTemplateProvider templateProvider,
            CriarOrdemServicoGateway criarGateway,
            CriarOrdemServicoPresenter criarPresenter,
            CriarProprietarioGateway criarProprietarioGateway,
            CriarVeiculoGateway criarVeiculoGateway,
            AdicionarServicoGateway adicionarGateway,
            AprovarOrdemServicoGateway aprovarGateway,
            AprovarOrdemServicoPresenter aprovarPresenter,
            BuscarOrdemServicoGateway buscarGateway,
            BuscarOrdemServicoPresenter buscarPresenter,
            BuscarOrdensPorStatusOrdenadoGateway buscarOrdensPorStatusGateway,
            BuscarOrdensPorStatusPresenter buscarOrdensPorStatusPresenter,
            BuscarTempoMedioExecucaoGateway buscarTempoMedioGateway,
            BuscarTempoMedioExecucaoPresenter buscarTempoMedioPresenter,
            IniciarDiagnosticoGateway iniciarDiagnosticoGateway,
            FinalizarDiagnosticoGateway finalizarDiagnosticoGateway,
            ReprovarOrdemServicoGateway reprovarGateway,
            IniciarServicoGateway iniciarServicoGateway,
            FinalizarServicoGateway finalizarServicoGateway,
            RetirarVeiculoGateway retirarVeiculoGateway,
            ReceberAprovacaoOrcamentoClienteGateway receberAprovacaoGateway,
            ReceberAprovacaoOrcamentoClientePresenter receberAprovacaoPresenter,
            EventPublisher eventPublisher,
            Logger logger) {

        this.logger = logger;
        this.notificarService = new NotificarOrdemServicoService(notificarGateway, emailProvider, templateProvider, logger);
        this.emailProvider = emailProvider;
        this.templateProvider = templateProvider;
        this.criarGateway = criarGateway;
        this.criarPresenter = criarPresenter;
        this.criarProprietarioGateway = criarProprietarioGateway;
        this.criarVeiculoGateway = criarVeiculoGateway;
        this.adicionarGateway = adicionarGateway;
        this.aprovarGateway = aprovarGateway;
        this.aprovarPresenter = aprovarPresenter;
        this.buscarGateway = buscarGateway;
        this.buscarPresenter = buscarPresenter;
        this.buscarOrdensPorStatusGateway = buscarOrdensPorStatusGateway;
        this.buscarOrdensPorStatusPresenter = buscarOrdensPorStatusPresenter;
        this.buscarTempoMedioGateway = buscarTempoMedioGateway;
        this.buscarTempoMedioPresenter = buscarTempoMedioPresenter;
        this.iniciarDiagnosticoGateway = iniciarDiagnosticoGateway;
        this.finalizarDiagnosticoGateway = finalizarDiagnosticoGateway;
        this.reprovarGateway = reprovarGateway;
        this.iniciarServicoGateway = iniciarServicoGateway;
        this.finalizarServicoGateway = finalizarServicoGateway;
        this.retirarVeiculoGateway = retirarVeiculoGateway;
        this.receberAprovacaoGateway = receberAprovacaoGateway;
        this.receberAprovacaoPresenter = receberAprovacaoPresenter;
        this.eventPublisher = eventPublisher;
    }

    public OrdemServicoResponse criar(CriarOrdemServicoUseCase.Input input) {
        logger.logInfo("Iniciando criação de ordem de serviço - proprietarioId={}, veiculoId={}", input.proprietarioId(), input.veiculoId());
        var useCase = new CriarOrdemServicoUseCase(criarGateway, notificarService, logger);
        var response = criarPresenter.present(useCase.execute(input));
        logger.logInfo("Criação de ordem de serviço concluída com sucesso - osId={}", response.id());
        return response;
    }

    public OrdemServicoResponse criarCompleta(CriarOrdemServicoCompletaUseCase.Input input) {
        logger.logInfo("Iniciando criação completa de ordem de serviço");
        var notificarCadastroProprietario = new NotificarCadastroProprietarioUseCase(emailProvider, templateProvider, logger);
        var useCase = new CriarOrdemServicoCompletaUseCase(
                criarGateway, criarProprietarioGateway, criarVeiculoGateway, adicionarGateway,
                notificarService, notificarCadastroProprietario, logger);
        var response = criarPresenter.present(useCase.execute(input));
        logger.logInfo("Criação completa de ordem de serviço concluída com sucesso - osId={}", response.id());
        return response;
    }

    public PaginatedResult<OrdemServicoResponse> buscar(Long id, StatusOrdemServico status,
                                                         Long proprietarioId, Long mecanicoId,
                                                         int page, int size) {
        logger.logInfo("Iniciando busca de ordens de serviço - id={}, status={}, proprietarioId={}, mecanicoId={}, page={}, size={}",
                id, status, proprietarioId, mecanicoId, page, size);
        var useCase = new BuscarOrdemServicoUseCase(buscarGateway, logger);
        var result = buscarPresenter.present(useCase.execute(id, status, proprietarioId, mecanicoId, page, size));
        logger.logInfo("Busca de ordens de serviço concluída com sucesso - totalElements={}", result.totalElements());
        return result;
    }

    public PaginatedResult<OrdemServicoResponse> buscarPorStatus(int page, int size) {
        logger.logInfo("Iniciando busca de ordens de serviço ordenadas por status - page={}, size={}", page, size);
        var useCase = new BuscarOrdensPorStatusOrdenadoUseCase(buscarOrdensPorStatusGateway, logger);
        var result = buscarOrdensPorStatusPresenter.present(useCase.execute(page, size));
        logger.logInfo("Busca de ordens de serviço ordenadas por status concluída com sucesso - totalElements={}", result.totalElements());
        return result;
    }

    public TempoMedioExecucaoResponse buscarTempoMedioExecucao(LocalDate dataInicio, LocalDate dataFim) {
        logger.logInfo("Iniciando busca de tempo médio de execução - dataInicio={}, dataFim={}", dataInicio, dataFim);
        var useCase = new BuscarTempoMedioExecucaoServicosFinalizadosUseCase(buscarTempoMedioGateway, logger);
        var result = useCase.execute(dataInicio, dataFim);
        var response = buscarTempoMedioPresenter.present(result.itens(), result.dataInicio(), result.dataFim());
        logger.logInfo("Busca de tempo médio de execução concluída com sucesso");
        return response;
    }

    public void iniciarDiagnostico(IniciarDiagnosticoUseCase.Input input) {
        logger.logInfo("Iniciando diagnóstico de ordem de serviço - osId={}", input.idOs());
        new IniciarDiagnosticoUseCase(iniciarDiagnosticoGateway, notificarService, logger).execute(input);
        logger.logInfo("Início de diagnóstico concluído com sucesso - osId={}", input.idOs());
    }

    public void adicionarServico(AdicionarServicoUseCase.Input input) {
        logger.logInfo("Iniciando adição de serviços à ordem de serviço - osId={}", input.osId());
        new AdicionarServicoUseCase(adicionarGateway, logger).execute(input);
        logger.logInfo("Adição de serviços concluída com sucesso - osId={}", input.osId());
    }

    public void finalizarDiagnostico(FinalizarDiagnosticoUseCase.Input input) {
        logger.logInfo("Iniciando finalização de diagnóstico - osId={}", input.idOs());
        new FinalizarDiagnosticoUseCase(finalizarDiagnosticoGateway, notificarService, eventPublisher, logger).execute(input);
        logger.logInfo("Finalização de diagnóstico concluída com sucesso - osId={}", input.idOs());
    }

    public AprovarOrdemServicoResponse aprovar(AprovarOrdemServicoUseCase.Input input) {
        logger.logInfo("Iniciando aprovação de ordem de serviço - osId={}", input.idOs());
        var useCase = new AprovarOrdemServicoUseCase(aprovarGateway, notificarService, logger);
        var response = aprovarPresenter.present(useCase.execute(input));
        logger.logInfo("Aprovação de ordem de serviço concluída com sucesso - osId={}", input.idOs());
        return response;
    }

    public void reprovar(ReprovarOrdemServicoUseCase.Input input) {
        logger.logInfo("Iniciando reprovação de ordem de serviço - osId={}", input.osId());
        new ReprovarOrdemServicoUseCase(reprovarGateway, notificarService, logger).execute(input);
        logger.logInfo("Reprovação de ordem de serviço concluída com sucesso - osId={}", input.osId());
    }

    public void iniciarServico(IniciarServicoUseCase.Input input) {
        logger.logInfo("Iniciando execução de serviço - osId={}, servicoId={}", input.idOs(), input.servicoId());
        new IniciarServicoUseCase(iniciarServicoGateway, notificarService, logger).execute(input);
        logger.logInfo("Início de execução de serviço concluído com sucesso - osId={}, servicoId={}", input.idOs(), input.servicoId());
    }

    public void finalizarServico(FinalizarServicoUseCase.Input input) {
        logger.logInfo("Iniciando finalização de serviço - osId={}, servicoId={}", input.osId(), input.servicoId());
        new FinalizarServicoUseCase(finalizarServicoGateway, notificarService, eventPublisher, logger).execute(input);
        logger.logInfo("Finalização de serviço concluída com sucesso - osId={}, servicoId={}", input.osId(), input.servicoId());
    }

    public void retirarVeiculo(Long idOs, UserId userId) {
        logger.logInfo("Iniciando retirada de veículo - osId={}", idOs);
        new RetirarVeiculoUseCase(retirarVeiculoGateway, notificarService, logger).execute(idOs, userId);
        logger.logInfo("Retirada de veículo concluída com sucesso - osId={}", idOs);
    }

    public ReceberAprovacaoOrcamentoClientePresenter.ViewModel receberAprovacaoOrcamentoCliente(
            ReceberAprovacaoOrcamentoClienteUseCase.Input input) {
        logger.logInfo("Iniciando recebimento de aprovação de orçamento pelo cliente - osId={}", input.osId());
        var useCase = new ReceberAprovacaoOrcamentoClienteUseCase(receberAprovacaoGateway, notificarService, logger);
        var response = receberAprovacaoPresenter.present(useCase.execute(input));
        logger.logInfo("Recebimento de aprovação de orçamento concluído com sucesso - osId={}", input.osId());
        return response;
    }
}
