package br.com.lata.velha.ordem_servico.application.controllers.servico;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordem_servico.application.presenters.servico.*;
import br.com.lata.velha.ordem_servico.application.use_cases.servico.*;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class ServicoCleanController {

    private final CadastrarServicoGateway cadastrarGateway;
    private final CadastrarServicoPresenter cadastrarPresenter;

    private final AtualizarServicoGateway atualizarGateway;
    private final AtualizarServicoPresenter atualizarPresenter;

    private final BuscarServicoPorIdGateway buscarPorIdGateway;
    private final BuscarServicoPorIdPresenter buscarPorIdPresenter;

    private final BuscarServicosGateway buscarTodosGateway;
    private final BuscarServicosPresenter buscarTodosPresenter;

    private final DesativarServicoGateway desativarGateway;

    private final Logger logger;

    public ServicoCleanController(CadastrarServicoGateway cadastrarGateway,
                                  CadastrarServicoPresenter cadastrarPresenter,
                                  AtualizarServicoGateway atualizarGateway,
                                  AtualizarServicoPresenter atualizarPresenter,
                                  BuscarServicoPorIdGateway buscarPorIdGateway,
                                  BuscarServicoPorIdPresenter buscarPorIdPresenter,
                                  BuscarServicosGateway buscarTodosGateway,
                                  BuscarServicosPresenter buscarTodosPresenter,
                                  DesativarServicoGateway desativarGateway,
                                  Logger logger) {
        this.cadastrarGateway = cadastrarGateway;
        this.cadastrarPresenter = cadastrarPresenter;
        this.atualizarGateway = atualizarGateway;
        this.atualizarPresenter = atualizarPresenter;
        this.buscarPorIdGateway = buscarPorIdGateway;
        this.buscarPorIdPresenter = buscarPorIdPresenter;
        this.buscarTodosGateway = buscarTodosGateway;
        this.buscarTodosPresenter = buscarTodosPresenter;
        this.desativarGateway = desativarGateway;
        this.logger = logger;
    }

    public ServicoResponse cadastrar(CadastrarServicoRequest request) {
        logger.logInfo("Iniciando cadastro de serviço");
        var servico = new CadastrarServicoUseCase(cadastrarGateway, logger).execute(request);
        logger.logInfo("Cadastro de serviço concluído com sucesso - servicoId={}", servico.getId());
        return cadastrarPresenter.present(servico);
    }

    public ServicoResponse atualizar(Long id, AtualizarServicoRequest request) {
        logger.logInfo("Iniciando atualização de serviço - servicoId={}", id);
        var servico = new AtualizarServicoUseCase(atualizarGateway, logger).execute(id, request);
        logger.logInfo("Atualização de serviço concluída com sucesso - servicoId={}", id);
        return atualizarPresenter.present(servico);
    }

    public ServicoResponse buscarPorId(Long id) {
        logger.logInfo("Iniciando busca de serviço por id - servicoId={}", id);
        var servico = new BuscarServicoPorIdUseCase(buscarPorIdGateway, logger).execute(id);
        logger.logInfo("Busca de serviço por id concluída com sucesso - servicoId={}", id);
        return buscarPorIdPresenter.present(servico);
    }

    public PaginatedResult<ServicoResponse> buscarTodos(int page, int size) {
        logger.logInfo("Iniciando busca de serviços - page={}, size={}", page, size);
        var result = new BuscarServicosUseCase(buscarTodosGateway, logger).execute(page, size);
        logger.logInfo("Busca de serviços concluída com sucesso - totalElements={}", result.totalElements());
        return buscarTodosPresenter.present(result);
    }

    public void desativar(Long id) {
        logger.logInfo("Iniciando desativação de serviço - servicoId={}", id);
        new DesativarServicoUseCase(desativarGateway, logger).execute(id);
        logger.logInfo("Desativação de serviço concluída com sucesso - servicoId={}", id);
    }
}
