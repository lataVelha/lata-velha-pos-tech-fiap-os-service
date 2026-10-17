package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.CargoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CargoJpaRepository extends JpaRepository<CargoEntity, Long> {
    @Query("SELECT c FROM CargoEntity c LEFT JOIN FETCH c.roles WHERE c.id = :id")
    Optional<CargoEntity> findByIdWithRoles(Long id);
}