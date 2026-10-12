package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.ProprietarioNotFoundException;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.VeiculoNotFoundException;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import br.com.lata.velha.ordem_servico.domain.value_objects.Placa;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ProprietarioEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.VeiculoPersistenceMapper;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class VeiculoRepositoryImpl implements VeiculoRepository {
    private final VeiculoJpaRepository jpaRepository;
    private final ProprietarioJpaRepository proprietarioJpaRepository;
    private final VeiculoPersistenceMapper mapper;
    private final Logger logger;

    @Override
    public Veiculo save(Veiculo veiculo) {
        if (veiculo.getId() == null) {
            validatePlacaAvailability(veiculo.getPlaca());
        }

        ProprietarioEntity proprietarioEntity = proprietarioJpaRepository
                .findById(veiculo.getProprietarioId())
                .orElseThrow(() -> {
                    logger.logWarn("Proprietário não encontrado ao salvar veículo - proprietarioId={}", veiculo.getProprietarioId());
                    return ProprietarioNotFoundException.fromId(veiculo.getProprietarioId());
                });

        var entity = mapper.toEntity(veiculo, proprietarioEntity);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Veiculo getActiveById(Long id) {
        return jpaRepository.findByIdAndAtivoTrue(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> {
                    logger.logWarn("Veículo ativo não encontrado - veiculoId={}", id);
                    return VeiculoNotFoundException.fromId(id);
                });
    }

    @Override
    public List<Veiculo> findActiveByProprietarioId(Long proprietarioId) {
        return jpaRepository.findByProprietarioIdAndAtivoTrue(proprietarioId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Veiculo> findAllActive() {
        return jpaRepository.findByAtivoTrue()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public PaginatedResult<Veiculo> findAllActivePaginated(int page, int size) {
        var result = jpaRepository.findByAtivoTrue(PageRequest.of(page, size));
        var content = result.getContent().stream().map(mapper::toDomain).toList();
        return new PaginatedResult<>(content, page, size,
                result.getTotalElements(), result.getTotalPages());
    }

    @Override
    public Veiculo findInactiveById(Long id) {
        return jpaRepository.findByIdAndAtivoFalse(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> {
                    logger.logWarn("Veículo inativo não encontrado - veiculoId={}", id);
                    return VeiculoNotFoundException.fromId(id);
                });
    }

    @Override
    public Veiculo getActiveByIdAndProprietarioId(Long id, Long proprietarioId) {
        return jpaRepository.findByIdAndProprietarioIdAndAtivoTrue(id, proprietarioId)
                .map(mapper::toDomain)
                .orElseThrow(() -> {
                    logger.logWarn("Veículo ativo não encontrado para o proprietário - veiculoId={}, proprietarioId={}", id, proprietarioId);
                    return VeiculoNotFoundException.fromId(id);
                });
    }

    private void validatePlacaAvailability(Placa placa) {
        if (jpaRepository.existsByPlaca(placa.getValor())) {
            logger.logWarn("Cadastro de veículo rejeitado: placa já cadastrada - placa={}", placa.getFormatted());
            throw new ResourceAlreadyExistsException(
                    "Já existe um veículo cadastrado com a placa: " + placa.getFormatted());
        }
    }
}