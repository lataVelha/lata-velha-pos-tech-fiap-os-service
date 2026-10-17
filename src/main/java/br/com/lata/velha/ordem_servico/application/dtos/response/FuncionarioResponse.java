package br.com.lata.velha.ordem_servico.application.dtos.response;

import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "FuncionarioResponse", description = "Dados do funcionário")
public record FuncionarioResponse(

        @Schema(description = "ID do funcionário", example = "1")
        Long id,

        @Schema(description = "Nome completo do funcionário", example = "João Silva")
        String nome,

        @Schema(description = "Cargo do funcionário", example = "MECANICO")
        String cargo,

        @Schema(description = "UUID do usuário de acesso vinculado", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
        UUID userId

) {
    public static FuncionarioResponse from(Funcionario funcionario) {
        return new FuncionarioResponse(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getCargo().getNome(),
                funcionario.getUserId().getValue()
        );
    }
}
