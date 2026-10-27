package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ProprietarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProprietarioJpaRepository extends JpaRepository<ProprietarioEntity, Long> {

    Optional<ProprietarioEntity> findByIdAndAtivoTrue(Long id);

    @Query("SELECT DISTINCT p FROM ProprietarioEntity p LEFT JOIN FETCH p.veiculos WHERE p.id = :id AND p.ativo = true")
    Optional<ProprietarioEntity> findByIdAndAtivoTrueWithVeiculos(@Param("id") Long id);

    Optional<ProprietarioEntity> findByIdAndAtivoFalse(Long id);

    Optional<ProprietarioEntity> findByDocumentoAndAtivoTrue(String documento);

    List<ProprietarioEntity> findByAtivoTrue();

    Page<ProprietarioEntity> findByAtivoTrue(Pageable pageable);

    boolean existsByDocumento(String documento);
}