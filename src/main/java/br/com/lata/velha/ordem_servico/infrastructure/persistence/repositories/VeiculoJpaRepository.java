package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.VeiculoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VeiculoJpaRepository extends JpaRepository<VeiculoEntity, Long> {

    Optional<VeiculoEntity> findByIdAndAtivoTrue(Long id);

    List<VeiculoEntity> findByProprietarioIdAndAtivoTrue(Long proprietarioId);

    List<VeiculoEntity> findByAtivoTrue();

    Page<VeiculoEntity> findByAtivoTrue(Pageable pageable);

    boolean existsByPlaca(String placa);

    Optional<VeiculoEntity> findByIdAndAtivoFalse(Long id);

    Optional<VeiculoEntity> findByIdAndProprietarioIdAndAtivoTrue(Long id, Long proprietarioId);
}