package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import java.util.Optional;

public interface CargoRepository {
    Cargo getById(Long id);
    Cargo getByIdWithRoles(Long id);
    Optional<Cargo> findById(Long id);
}