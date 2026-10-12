package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;

import java.util.List;
import java.util.Set;

public interface ExecucaoServicoRepository {

    ExecucaoServico save(ExecucaoServico execucaoServico);

    List<ExecucaoServico> saveAll(List<ExecucaoServico> execucoes);

    ExecucaoServico findById(Long id);

    Set<ExecucaoServico> getAllByIdWithPeca(Set<Long> ids);

}