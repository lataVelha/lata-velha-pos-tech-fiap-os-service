package br.com.lata.velha.ordem_servico.infrastructure.persistence.gateways;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.*;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.*;
import br.com.lata.velha.ordem_servico.domain.entities.*;
import br.com.lata.velha.ordem_servico.domain.repositories.*;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class OrdemServicoGatewayImpl implements
        CriarOrdemServicoGateway,
        AprovarOrdemServicoGateway,
        BuscarOrdemServicoGateway,
        BuscarOrdensPorStatusOrdenadoGateway,
        BuscarTempoMedioExecucaoGateway,
        AdicionarServicoGateway,
        IniciarDiagnosticoGateway,
        FinalizarDiagnosticoGateway,
        IniciarServicoGateway,
        FinalizarServicoGateway,
        ReprovarOrdemServicoGateway,
        RetirarVeiculoGateway,
        NotificarOrdemServicoGateway,
        ReceberAprovacaoOrcamentoClienteGateway,
        ReceberInventoryCheckedGateway,
        ReceberOrcamentoReprovadoGateway,
        ReceberPecaCompletamenteAlocadaGateway,
        ReceberPaymentDoneGateway {

    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ProprietarioRepository proprietarioRepository;
    private final VeiculoRepository veiculoRepository;
    private final ServicoRepository servicoRepository;
    private final ExecucaoServicoRepository execucaoServicoRepository;
    private final ExecucaoServicoMetricaRepository execucaoServicoMetricaRepository;
    private final Logger logger;

    @Override
    public ExecucaoServico getExecucaoServicoPorId(Long id) {
        return execucaoServicoRepository.findById(id);
    }

    @Override
    public Proprietario getProprietarioAtivoPorId(Long id) {
        return proprietarioRepository.getActiveById(id);
    }

    @Override
    public Proprietario getProprietarioPorId(Long id) {
        return proprietarioRepository.getActiveByIdWithVeiculos(id);
    }

    @Override
    public Veiculo getVeiculoAtivoDoProprietario(Long veiculoId, Long proprietarioId) {
        return veiculoRepository.getActiveByIdAndProprietarioId(veiculoId, proprietarioId);
    }

    @Override
    public Veiculo getVeiculoPorId(Long id) {
        return veiculoRepository.getActiveById(id);
    }

    @Override
    public Funcionario getFuncionarioPorUserId(UserId userId) {
        return funcionarioRepository.getByUserId(userId);
    }

    @Override
    public OrdemServico salvarOrdemServico(OrdemServico os) {
        return ordemServicoRepository.save(os);
    }

    @Override
    public OrdemServicoProjection getOrdemServicoProjectionById(Long id) {
        return ordemServicoRepository.findByAllOrdemSevico(id, null, null, null, 0, 1)
                .content()
                .stream()
                .findFirst()
                .orElseThrow(() -> {
                    logger.logWarn("Projeção de ordem de serviço não encontrada - osId={}", id);
                    return new NoSuchElementException("OrdemServico não encontrada: " + id);
                });
    }

    @Override
    public OrdemServico getOrdemServicoComServicosEPecas(Long id) {
        return ordemServicoRepository.getByIdWithExecucoesAndPecas(id);
    }

    @Override
    public OrdemServico getOrdemServicoPorId(Long id) {
        return ordemServicoRepository.getById(id);
    }

    @Override
    public OrdemServico getOrdemServicoComServicos(Long id) {
        return ordemServicoRepository.getById(id);
    }

    @Override
    public List<Servico> getServicosAtivosPorIds(Set<Long> ids) {
        return new ArrayList<>(servicoRepository.getAllActiveById(ids));
    }

    @Override
    public Servico getServicoAtivoPorId(Long id) {
        return servicoRepository.getActiveById(id);
    }

    @Override
    public PaginatedResult<OrdemServicoProjection> findByFiltros(Long id, String status, Long proprietarioId, Long mecanicoId, int page, int size) {
        return ordemServicoRepository.findByAllOrdemSevico(id, status, proprietarioId, mecanicoId, page, size);
    }

    @Override
    public PaginatedResult<OrdemServicoProjection> findOrderedByStatusPriority(int page, int size) {
        return ordemServicoRepository.findOrderedByStatusPriority(page, size);
    }

    @Override
    public List<TempoMedioExecucaoPorServico> buscarTempoMedioExecucao(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return execucaoServicoMetricaRepository.buscarTempoMedioExecucaoServicosFinalizados(dataInicio, dataFim);
    }
}
