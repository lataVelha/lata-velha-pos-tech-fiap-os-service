package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.FuncionarioNotFoundException;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.FuncionarioPersistenceMapper;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FuncionarioRepositoryImpl implements FuncionarioRepository {
    private final FuncionarioJpaRepository jpaRepository;
    private final FuncionarioPersistenceMapper mapper;
    private final Logger logger;

    @Override
    public Funcionario save(Funcionario funcionario) {
        var entity = mapper.toEntity(funcionario);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Funcionario> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Funcionario getById(Long id) {
        return findById(id)
                .orElseThrow(() -> {
                    logger.logWarn("Funcionário não encontrado - funcionarioId={}", id);
                    return FuncionarioNotFoundException.fromId(id);
                });
    }

    @Override
    public Funcionario getByUserId(UserId userId) {
        return jpaRepository.findByUserId(userId.getValue())
                .map(mapper::toDomain)
                .orElseThrow(() -> {
                    logger.logWarn("Funcionário não encontrado pelo userId - userId={}", userId.getValue());
                    return FuncionarioNotFoundException.fromUserId(userId);
                });
    }

    @Override
    public List<Funcionario> findAllByCargoNome(String cargoNome) {
        return jpaRepository.findAllByCargo_Nome(cargoNome).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        return jpaRepository.existsByUserId(userId.getValue());
    }
}