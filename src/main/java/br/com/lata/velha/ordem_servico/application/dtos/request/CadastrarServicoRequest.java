package br.com.lata.velha.ordem_servico.application.dtos.request;

import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import jakarta.validation.constraints.NotBlank;

public record CadastrarServicoRequest(
        @NotBlank(message = "Nome é obrigatório") String nome,
        @NotBlank(message = "Descrição é obrigatória") String descricao
) {
    public Servico toDomain() {
        return new Servico(null, nome, descricao, true);
    }
}
