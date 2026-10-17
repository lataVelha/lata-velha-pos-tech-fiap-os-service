package br.com.lata.velha.authentication.infrastructure.persistence.jpa;

import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RoleJpaRepository extends JpaRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByNome(String nome);
    Set<RoleEntity> findAllByNomeIn(Set<String> names);
}
