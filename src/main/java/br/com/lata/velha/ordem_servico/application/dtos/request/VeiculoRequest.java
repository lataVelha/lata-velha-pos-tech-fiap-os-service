package br.com.lata.velha.ordem_servico.application.dtos.request;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.value_objects.Placa;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para criação/atualização de veículo")
public record VeiculoRequest(
        @NotNull(message = "ID do proprietário é obrigatório") @Schema(example = "1") Long proprietarioId,
        @NotBlank(message = "Placa é obrigatória") @Schema(example = "ABC1D23") String placa,
        @NotBlank(message = "Marca é obrigatória") @Schema(example = "Fiat") String marca,
        @NotBlank(message = "Modelo é obrigatório") @Schema(example = "Uno") String modelo,
        @NotNull(message = "Ano é obrigatório") @Schema(example = "2020") Integer ano,
        @NotBlank(message = "Cor é obrigatória") @Schema(example = "Prata") String cor
) {
    public Veiculo toDomain() {
        return new Veiculo(null, proprietarioId, Placa.of(placa), marca, modelo, ano, cor);
    }

    public void updateDomain(Veiculo existente) {
        existente.setProprietarioId(proprietarioId);
        existente.setPlaca(Placa.of(placa));
        existente.setMarca(marca);
        existente.setModelo(modelo);
        existente.setAno(ano);
        existente.setCor(cor);
    }
}
