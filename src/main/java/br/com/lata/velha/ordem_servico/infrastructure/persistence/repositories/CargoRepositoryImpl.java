package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.CargoNotFoundException;
import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.ordem_servico.domain.repositories.CargoRepository;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.FuncionarioPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CargoRepositoryImpl implements CargoRepository {
    private final CargoJpaRepository jpaRepository;
    private final FuncionarioPersistenceMapper mapper;

    @Override
    public Cargo getById(Long id) {
        return findById(id)
                .orElseThrow(() -> CargoNotFoundException.fromId(id));
    }

    @Override
    public Cargo getByIdWithRoles(Long id) {
        return jpaRepository.findByIdWithRoles(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> CargoNotFoundException.fromId(id));
    }

    @Override
    public Optional<Cargo> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}