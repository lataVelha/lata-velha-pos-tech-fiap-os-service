package br.com.lata.velha.ordem_servico.application.controllers.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.shared.application.gateways.EmailProvider;
import br.com.lata.velha.shared.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.application.presenters.proprietario.*;
import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.*;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class ProprietarioCleanController {

    private final CriarProprietarioGateway criarGateway;
    private final CriarProprietarioPresenter criarPresenter;

    private final AtualizarProprietarioGateway atualizarGateway;
    private final AtualizarProprietarioPresenter atualizarPresenter;

    private final BuscarProprietarioPorIdGateway buscarPorIdGateway;
    private final BuscarProprietarioPorIdPresenter buscarPorIdPresenter;

    private final BuscarProprietarioPorDocumentoGateway buscarPorDocumentoGateway;
    private final BuscarProprietarioPorDocumentoPresenter buscarPorDocumentoPresenter;

    private final ListarProprietariosGateway listarGateway;
    private final ListarProprietariosPresenter listarPresenter;

    private final DesativarProprietarioGateway desativarGateway;

    private final ReativarProprietarioGateway reativarGateway;
    private final ReativarProprietarioPresenter reativarPresenter;

    private final EmailProvider emailProvider;
    private final EmailTemplateProvider templateProvider;

    private final Logger logger;

    public ProprietarioCleanController(CriarProprietarioGateway criarGateway,
                                       CriarProprietarioPresenter criarPresenter,
                                       AtualizarProprietarioGateway atualizarGateway,
                                       AtualizarProprietarioPresenter atualizarPresenter,
                                       BuscarProprietarioPorIdGateway buscarPorIdGateway,
                                       BuscarProprietarioPorIdPresenter buscarPorIdPresenter,
                                       BuscarProprietarioPorDocumentoGateway buscarPorDocumentoGateway,
                                       BuscarProprietarioPorDocumentoPresenter buscarPorDocumentoPresenter,
                                       ListarProprietariosGateway listarGateway,
                                       ListarProprietariosPresenter listarPresenter,
                                       DesativarProprietarioGateway desativarGateway,
                                       ReativarProprietarioGateway reativarGateway,
                                       ReativarProprietarioPresenter reativarPresenter,
                                       EmailProvider emailProvider,
                                       EmailTemplateProvider templateProvider,
                                       Logger logger) {
        this.criarGateway = criarGateway;
        this.criarPresenter = criarPresenter;
        this.atualizarGateway = atualizarGateway;
        this.atualizarPresenter = atualizarPresenter;
        this.buscarPorIdGateway = buscarPorIdGateway;
        this.buscarPorIdPresenter = buscarPorIdPresenter;
        this.buscarPorDocumentoGateway = buscarPorDocumentoGateway;
        this.buscarPorDocumentoPresenter = buscarPorDocumentoPresenter;
        this.listarGateway = listarGateway;
        this.listarPresenter = listarPresenter;
        this.desativarGateway = desativarGateway;
        this.reativarGateway = reativarGateway;
        this.reativarPresenter = reativarPresenter;
        this.emailProvider = emailProvider;
        this.templateProvider = templateProvider;
        this.logger = logger;
    }

    public ProprietarioResponse criar(ProprietarioRequest request) {
        logger.logInfo("Iniciando criação de proprietário");
        var notificar = new NotificarCadastroProprietarioUseCase(emailProvider, templateProvider, logger);
        var proprietario = new CriarProprietarioUseCase(criarGateway, notificar, logger).execute(request);
        logger.logInfo("Criação de proprietário concluída com sucesso - proprietarioId={}", proprietario.getId());
        return criarPresenter.present(proprietario);
    }

    public ProprietarioResponse atualizar(Long id, ProprietarioRequest request) {
        logger.logInfo("Iniciando atualização de proprietário - proprietarioId={}", id);
        var proprietario = new AtualizarProprietarioUseCase(atualizarGateway, logger).execute(id, request);
        logger.logInfo("Atualização de proprietário concluída com sucesso - proprietarioId={}", id);
        return atualizarPresenter.present(proprietario);
    }

    public ProprietarioResponse buscarPorId(Long id) {
        logger.logInfo("Iniciando busca de proprietário por id - proprietarioId={}", id);
        var proprietario = new BuscarProprietarioPorIdUseCase(buscarPorIdGateway, logger).execute(id);
        logger.logInfo("Busca de proprietário por id concluída com sucesso - proprietarioId={}", id);
        return buscarPorIdPresenter.present(proprietario);
    }

    public ProprietarioResponse buscarPorDocumento(String documento) {
        logger.logInfo("Iniciando busca de proprietário por documento");
        var proprietario = new BuscarProprietarioPorDocumentoUseCase(buscarPorDocumentoGateway, logger).execute(documento);
        logger.logInfo("Busca de proprietário por documento concluída com sucesso - proprietarioId={}", proprietario.getId());
        return buscarPorDocumentoPresenter.present(proprietario);
    }

    public PaginatedResult<ProprietarioResponse> listar(int page, int size) {
        logger.logInfo("Iniciando listagem de proprietários - page={}, size={}", page, size);
        var result = new ListarProprietariosUseCase(listarGateway, logger).execute(page, size);
        logger.logInfo("Listagem de proprietários concluída com sucesso - totalElements={}", result.totalElements());
        return listarPresenter.present(result);
    }

    public void desativar(Long id) {
        logger.logInfo("Iniciando desativação de proprietário - proprietarioId={}", id);
        new DesativarProprietarioUseCase(desativarGateway, logger).execute(id);
        logger.logInfo("Desativação de proprietário concluída com sucesso - proprietarioId={}", id);
    }

    public ProprietarioResponse reativar(Long id) {
        logger.logInfo("Iniciando reativação de proprietário - proprietarioId={}", id);
        var proprietario = new ReativarProprietarioUseCase(reativarGateway, logger).execute(id);
        logger.logInfo("Reativação de proprietário concluída com sucesso - proprietarioId={}", id);
        return reativarPresenter.present(proprietario);
    }
}
