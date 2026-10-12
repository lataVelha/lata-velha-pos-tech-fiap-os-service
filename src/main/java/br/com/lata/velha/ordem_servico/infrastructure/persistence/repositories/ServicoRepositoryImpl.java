package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.ServicoNotFoundException;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ServicoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.ServicoPersistenceMapper;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ServicoRepositoryImpl implements ServicoRepository {
    private final ServicoJpaRepository jpaRepository;
    private final ServicoPersistenceMapper mapper;
    private final Logger logger;

    @Override
    public Servico save(Servico servico) {
        var entity = mapper.toEntity(servico);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Servico getActiveById(Long id) {
        return jpaRepository.findByIdAndAtivoTrue(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> {
                    logger.logWarn("Serviço ativo não encontrado - servicoId={}", id);
                    return ServicoNotFoundException.fromId(id);
                });
    }

    @Override
    public PaginatedResult<Servico> findAllActivePaginated(int page, int size) {
        var result = jpaRepository.findByAtivoTrue(PageRequest.of(page, size));
        var content = result.getContent().stream().map(mapper::toDomain).toList();

        return new PaginatedResult<>(
                content,
                page,
                size,
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public Set<Servico> getAllActiveById(Set<Long> servicoIds) {
        return jpaRepository.getAllByIdInAndAtivoTrue(servicoIds).stream()
                .map(ServicoEntity::toDomain)
                .collect(Collectors.toSet());
    }
}
