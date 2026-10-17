package br.com.lata.velha.ordem_servico.application.dtos.response;

import br.com.lata.velha.ordem_servico.domain.entities.PecaNecessaria;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PecaNecessariaResponse(

        @Schema(example = "10")
        Long id,

        @Schema(example = "1")
        Long pecaId,

        @Schema(example = "4")
        Integer quantidade,

        @Schema(example = "2026-04-11T12:00:00")
        LocalDateTime atualizado

) {
    public static PecaNecessariaResponse from(PecaNecessaria domain) {
        return new PecaNecessariaResponse(
                domain.getId(),
                domain.getPecaId(),
                domain.getQuantidade(),
                domain.getAtualizado()
        );
    }
}
