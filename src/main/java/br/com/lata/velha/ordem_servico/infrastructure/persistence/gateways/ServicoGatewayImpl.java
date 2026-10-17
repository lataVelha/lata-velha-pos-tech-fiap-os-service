package br.com.lata.velha.ordem_servico.infrastructure.persistence.gateways;

import br.com.lata.velha.ordem_servico.application.use_cases.servico.*;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServicoGatewayImpl implements
        CadastrarServicoGateway,
        AtualizarServicoGateway,
        BuscarServicoPorIdGateway,
        BuscarServicosGateway,
        DesativarServicoGateway {

    private final ServicoRepository servicoRepository;

    @Override
    public Servico salvarServico(Servico s) {
        return servicoRepository.save(s);
    }

    @Override
    public Servico getServicoPorId(Long id) {
        return servicoRepository.getActiveById(id);
    }

    @Override
    public PaginatedResult<Servico> findAll(int page, int size) {
        return servicoRepository.findAllActivePaginated(page, size);
    }
}
