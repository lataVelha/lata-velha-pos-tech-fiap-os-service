package br.com.lata.velha.ordem_servico.application.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FuncionarioResumoResponse")
public record FuncionarioResumoResponse(

        @Schema(description = "Id do funcionário", example = "1")
        Long id,

        @Schema(description = "Nome do funcionário", example = "João Silva")
        String nome

) {}
