package br.com.lata.velha.authentication.infrastructure.persistence.repositories;

import br.com.lata.velha.authentication.domain.entities.Role;
import br.com.lata.velha.authentication.domain.exceptions.not_found_exceptions.RoleNotFoundException;
import br.com.lata.velha.authentication.domain.repositories.RoleRepository;
import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.authentication.infrastructure.persistence.jpa.RoleJpaRepository;
import br.com.lata.velha.shared.application.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {
    private final RoleJpaRepository jpaRepository;
    private final Logger logger;

    @Override
    public Role getByNome(String nome) {
        return jpaRepository.findByNome(nome)
                .orElseThrow(() -> {
                    logger.logWarn("Role não encontrada - nome={}", nome);
                    return RoleNotFoundException.fromNome(nome);
                })
                .toDomain();
    }

    @Override
    public Set<Role> getByNomes(Set<String> names) {
        return jpaRepository.findAllByNomeIn(names)
                .stream()
                .map(RoleEntity::toDomain)
                .collect(Collectors.toSet());
    }
}
