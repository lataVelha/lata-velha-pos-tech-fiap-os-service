package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

import java.util.List;

public interface ProprietarioRepository {

    Proprietario save(Proprietario proprietario);

    Proprietario getActiveById(Long id);

    Proprietario findActiveByDocumento(String documento);

    Proprietario findInactiveById(Long id);

    List<Proprietario> findAllActive();

    PaginatedResult<Proprietario> findAllActivePaginated(int page, int size);

    boolean existsByDocumento(String documento);
}