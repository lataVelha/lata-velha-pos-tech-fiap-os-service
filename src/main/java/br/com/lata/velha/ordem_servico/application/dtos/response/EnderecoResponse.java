package br.com.lata.velha.ordem_servico.application.dtos.response;

import br.com.lata.velha.ordem_servico.domain.value_objects.Endereco;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do endereço")
public record EnderecoResponse(
        @Schema(example = "Rua das Flores") String rua,
        @Schema(example = "01234567") String cep,
        @Schema(example = "123") String numeroCasa
) {
    public static EnderecoResponse from(Endereco endereco) {
        if (endereco == null) return null;
        return new EnderecoResponse(endereco.getRua(), endereco.getCep(), endereco.getNumeroCasa());
    }
}
