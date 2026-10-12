package br.com.lata.velha.ordem_servico.application.controllers.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.application.presenters.veiculo.*;
import br.com.lata.velha.ordem_servico.application.use_cases.veiculo.*;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

import java.util.List;

public class VeiculoCleanController {

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

    private final Logger logger;

    public VeiculoCleanController(CriarVeiculoGateway criarGateway,
                                  CriarVeiculoPresenter criarPresenter,
                                  AtualizarVeiculoGateway atualizarGateway,
                                  AtualizarVeiculoPresenter atualizarPresenter,
                                  BuscarVeiculoPorIdGateway buscarPorIdGateway,
                                  BuscarVeiculoPorIdPresenter buscarPorIdPresenter,
                                  ListarVeiculosGateway listarGateway,
                                  ListarVeiculosPresenter listarPresenter,
                                  ListarVeiculosPorProprietarioGateway listarPorProprietarioGateway,
                                  ListarVeiculosPorProprietarioPresenter listarPorProprietarioPresenter,
                                  DesativarVeiculoGateway desativarGateway,
                                  ReativarVeiculoGateway reativarGateway,
                                  ReativarVeiculoPresenter reativarPresenter,
                                  Logger logger) {
        this.criarGateway = criarGateway;
        this.criarPresenter = criarPresenter;
        this.atualizarGateway = atualizarGateway;
        this.atualizarPresenter = atualizarPresenter;
        this.buscarPorIdGateway = buscarPorIdGateway;
        this.buscarPorIdPresenter = buscarPorIdPresenter;
        this.listarGateway = listarGateway;
        this.listarPresenter = listarPresenter;
        this.listarPorProprietarioGateway = listarPorProprietarioGateway;
        this.listarPorProprietarioPresenter = listarPorProprietarioPresenter;
        this.desativarGateway = desativarGateway;
        this.reativarGateway = reativarGateway;
        this.reativarPresenter = reativarPresenter;
        this.logger = logger;
    }

    public VeiculoResponse criar(VeiculoRequest request) {
        logger.logInfo("Iniciando criação de veículo - proprietarioId={}", request.proprietarioId());
        var veiculo = new CriarVeiculoUseCase(criarGateway, logger).execute(request);
        logger.logInfo("Criação de veículo concluída com sucesso - veiculoId={}", veiculo.getId());
        return criarPresenter.present(veiculo);
    }

    public VeiculoResponse atualizar(Long id, VeiculoRequest request) {
        logger.logInfo("Iniciando atualização de veículo - veiculoId={}", id);
        var veiculo = new AtualizarVeiculoUseCase(atualizarGateway, logger).execute(id, request);
        logger.logInfo("Atualização de veículo concluída com sucesso - veiculoId={}", id);
        return atualizarPresenter.present(veiculo);
    }

    public VeiculoResponse buscarPorId(Long id) {
        logger.logInfo("Iniciando busca de veículo por id - veiculoId={}", id);
        var veiculo = new BuscarVeiculoPorIdUseCase(buscarPorIdGateway, logger).execute(id);
        logger.logInfo("Busca de veículo por id concluída com sucesso - veiculoId={}", id);
        return buscarPorIdPresenter.present(veiculo);
    }

    public PaginatedResult<VeiculoResponse> listar(int page, int size) {
        logger.logInfo("Iniciando listagem de veículos - page={}, size={}", page, size);
        var result = new ListarVeiculosUseCase(listarGateway, logger).execute(page, size);
        logger.logInfo("Listagem de veículos concluída com sucesso - totalElements={}", result.totalElements());
        return listarPresenter.present(result);
    }

    public List<VeiculoResponse> listarPorProprietario(Long proprietarioId) {
        logger.logInfo("Iniciando listagem de veículos por proprietário - proprietarioId={}", proprietarioId);
        var result = new ListarVeiculosPorProprietarioUseCase(listarPorProprietarioGateway, logger).execute(proprietarioId);
        logger.logInfo("Listagem de veículos por proprietário concluída com sucesso - proprietarioId={}, quantidade={}", proprietarioId, result.size());
        return listarPorProprietarioPresenter.present(result);
    }

    public void desativar(Long id) {
        logger.logInfo("Iniciando desativação de veículo - veiculoId={}", id);
        new DesativarVeiculoUseCase(desativarGateway, logger).execute(id);
        logger.logInfo("Desativação de veículo concluída com sucesso - veiculoId={}", id);
    }

    public VeiculoResponse reativar(Long id) {
        logger.logInfo("Iniciando reativação de veículo - veiculoId={}", id);
        var veiculo = new ReativarVeiculoUseCase(reativarGateway, logger).execute(id);
        logger.logInfo("Reativação de veículo concluída com sucesso - veiculoId={}", id);
        return reativarPresenter.present(veiculo);
    }
}
