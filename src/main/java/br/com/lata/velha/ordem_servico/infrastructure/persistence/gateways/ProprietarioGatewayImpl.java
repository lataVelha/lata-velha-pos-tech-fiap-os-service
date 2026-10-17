package br.com.lata.velha.ordem_servico.infrastructure.persistence.gateways;

import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.*;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProprietarioGatewayImpl implements
        CriarProprietarioGateway,
        AtualizarProprietarioGateway,
        BuscarProprietarioPorIdGateway,
        BuscarProprietarioPorDocumentoGateway,
        DesativarProprietarioGateway,
        ReativarProprietarioGateway,
        ListarProprietariosGateway,
        NotificarCadastroProprietarioGateway {

    private final ProprietarioRepository proprietarioRepository;

    @Override
    public Proprietario salvarProprietario(Proprietario p) {
        return proprietarioRepository.save(p);
    }

    @Override
    public Proprietario getProprietarioPorId(Long id) {
        return proprietarioRepository.getActiveById(id);
    }

    @Override
    public Proprietario getProprietarioInativoPorId(Long id) {
        return proprietarioRepository.findInactiveById(id);
    }

    @Override
    public Proprietario getProprietarioPorDocumento(String documento) {
        return proprietarioRepository.findActiveByDocumento(documento);
    }

    @Override
    public PaginatedResult<Proprietario> findAll(int page, int size) {
        return proprietarioRepository.findAllActivePaginated(page, size);
    }
}
