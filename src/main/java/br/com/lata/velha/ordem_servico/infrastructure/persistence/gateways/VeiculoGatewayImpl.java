package br.com.lata.velha.ordem_servico.infrastructure.persistence.gateways;

import br.com.lata.velha.ordem_servico.application.use_cases.veiculo.*;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VeiculoGatewayImpl implements
        CriarVeiculoGateway,
        AtualizarVeiculoGateway,
        BuscarVeiculoPorIdGateway,
        DesativarVeiculoGateway,
        ReativarVeiculoGateway,
        ListarVeiculosPorProprietarioGateway,
        ListarVeiculosGateway {

    private final VeiculoRepository veiculoRepository;
    private final ProprietarioRepository proprietarioRepository;

    @Override
    public Proprietario getProprietarioAtivoPorId(Long id) {
        return proprietarioRepository.getActiveById(id);
    }

    @Override
    public Veiculo salvarVeiculo(Veiculo v) {
        return veiculoRepository.save(v);
    }

    @Override
    public Veiculo getVeiculoPorId(Long id) {
        return veiculoRepository.getActiveById(id);
    }

    @Override
    public Veiculo getVeiculoInativoPorId(Long id) {
        return veiculoRepository.findInactiveById(id);
    }

    @Override
    public List<Veiculo> findByProprietarioId(Long proprietarioId) {
        return veiculoRepository.findActiveByProprietarioId(proprietarioId);
    }

    @Override
    public PaginatedResult<Veiculo> findAll(int page, int size) {
        return veiculoRepository.findAllActivePaginated(page, size);
    }
}
