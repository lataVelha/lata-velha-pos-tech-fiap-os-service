package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.shared.domain.value_objects.UserId;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository {

    Funcionario save(Funcionario funcionario);

    Optional<Funcionario> findById(Long id);

    Funcionario getById(Long id);

    Funcionario getByUserId(UserId userId);

    List<Funcionario> findAllByCargoNome(String cargoNome);

    boolean existsByUserId(UserId userId);
}