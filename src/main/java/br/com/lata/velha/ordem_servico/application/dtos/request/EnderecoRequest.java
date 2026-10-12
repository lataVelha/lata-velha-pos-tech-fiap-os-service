package br.com.lata.velha.ordem_servico.application.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados do endereço")
public record EnderecoRequest(
        @NotBlank(message = "Rua é obrigatória") @Schema(example = "Rua das Flores") String rua,
        @NotBlank(message = "CEP é obrigatório") @Schema(example = "01234-567") String cep,
        @NotBlank(message = "Número é obrigatório") @Schema(example = "123") String numeroCasa
) {}