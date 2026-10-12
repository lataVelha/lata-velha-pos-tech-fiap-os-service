package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.ProprietarioNotFoundException;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.ProprietarioPersistenceMapper;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProprietarioRepositoryImpl implements ProprietarioRepository {

    private final ProprietarioJpaRepository jpaRepository;
    private final ProprietarioPersistenceMapper mapper;
    private final Logger logger;

    @Override
    public Proprietario save(Proprietario proprietario) {
        if (proprietario.getId() == null) {
            validateDocumentoAvailability(proprietario.getDocumento().getValor());
        }
        var entity = mapper.toEntity(proprietario);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Proprietario getActiveById(Long id) {
        return jpaRepository.findByIdAndAtivoTrue(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> {
                    logger.logWarn("Proprietário ativo não encontrado - proprietarioId={}", id);
                    return ProprietarioNotFoundException.fromId(id);
                });
    }

    @Override
    public Proprietario findActiveByDocumento(String documento) {
        return jpaRepository.findByDocumentoAndAtivoTrue(documento)
                .map(mapper::toDomain)
                .orElseThrow(() -> {
                    logger.logWarn("Proprietário ativo não encontrado pelo documento informado");
                    return ProprietarioNotFoundException.fromDocumento(documento);
                });
    }

    @Override
    public Proprietario findInactiveById(Long id) {
        return jpaRepository.findByIdAndAtivoFalse(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> {
                    logger.logWarn("Proprietário inativo não encontrado - proprietarioId={}", id);
                    return ProprietarioNotFoundException.fromId(id);
                });
    }

    @Override
    public List<Proprietario> findAllActive() {
        return jpaRepository.findByAtivoTrue()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public PaginatedResult<Proprietario> findAllActivePaginated(int page, int size) {
        var result = jpaRepository.findByAtivoTrue(PageRequest.of(page, size));
        var content = result.getContent().stream().map(mapper::toDomain).toList();
        return new PaginatedResult<>(content, page, size,
                result.getTotalElements(), result.getTotalPages());
    }

    @Override
    public boolean existsByDocumento(String documento) {
        return jpaRepository.existsByDocumento(documento);
    }

    // --- private ---

    private void validateDocumentoAvailability(String documento) {
        if (jpaRepository.existsByDocumento(documento)) {
            logger.logWarn("Cadastro de proprietário rejeitado: documento já cadastrado");
            throw new ResourceAlreadyExistsException(
                    "Já existe um proprietário cadastrado com este documento");
        }
    }
}