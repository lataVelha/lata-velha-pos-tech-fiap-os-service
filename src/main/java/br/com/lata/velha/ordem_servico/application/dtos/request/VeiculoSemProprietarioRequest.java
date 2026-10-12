package br.com.lata.velha.ordem_servico.application.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados do veículo, sem o proprietário (preenchido a partir do proprietário criado na mesma requisição)")
public record VeiculoSemProprietarioRequest(
        @NotBlank(message = "Placa é obrigatória") @Schema(example = "ABC1D23") String placa,
        @NotBlank(message = "Marca é obrigatória") @Schema(example = "Fiat") String marca,
        @NotBlank(message = "Modelo é obrigatório") @Schema(example = "Uno") String modelo,
        @NotNull(message = "Ano é obrigatório") @Schema(example = "2020") Integer ano,
        @NotBlank(message = "Cor é obrigatória") @Schema(example = "Prata") String cor
) {
    public VeiculoRequest toVeiculoRequest(Long proprietarioId) {
        return new VeiculoRequest(proprietarioId, placa, marca, modelo, ano, cor);
    }
}
