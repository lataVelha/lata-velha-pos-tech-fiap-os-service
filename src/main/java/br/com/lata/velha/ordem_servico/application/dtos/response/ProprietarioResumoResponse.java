package br.com.lata.velha.ordem_servico.application.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProprietarioResumoResponse")
public record ProprietarioResumoResponse(

        @Schema(description = "Id do proprietário", example = "4")
        Long id,

        @Schema(description = "Nome do proprietário", example = "João Santos")
        String nome

) {}
