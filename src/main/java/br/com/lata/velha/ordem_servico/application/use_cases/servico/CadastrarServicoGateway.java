package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.domain.entities.Servico;

public interface CadastrarServicoGateway {
    Servico salvarServico(Servico s);
}
