package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

import java.util.List;

public interface VeiculoRepository {

    Veiculo save(Veiculo veiculo);

    Veiculo getActiveById(Long id);

    List<Veiculo> findActiveByProprietarioId(Long proprietarioId);

    List<Veiculo> findAllActive();

    PaginatedResult<Veiculo> findAllActivePaginated(int page, int size);
    
    Veiculo findInactiveById(Long id);

    Veiculo getActiveByIdAndProprietarioId(Long id, Long proprietarioId);
}