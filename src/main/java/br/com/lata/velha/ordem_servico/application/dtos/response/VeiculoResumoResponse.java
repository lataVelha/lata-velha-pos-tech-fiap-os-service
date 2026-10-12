package br.com.lata.velha.ordem_servico.application.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "VeiculoResumoResponse")
public record VeiculoResumoResponse(

        @Schema(description = "Id do veículo", example = "7")
        Long id,

        @Schema(description = "Descrição do veículo", example = "Fiat Uno 2020")
        String descricao

) {}
