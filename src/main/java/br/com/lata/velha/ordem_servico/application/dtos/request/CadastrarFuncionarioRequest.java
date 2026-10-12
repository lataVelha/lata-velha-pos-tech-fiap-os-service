package br.com.lata.velha.ordem_servico.application.dtos.request;

import br.com.lata.velha.ordem_servico.application.use_cases.funcionario.CadastrarFuncionarioUseCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CadastrarFuncionarioRequest(
    @NotBlank(message = "Nome é obrigatório") String nome,
    @NotBlank(message = "Username é obrigatório") String username,
    @NotBlank(message = "Senha é obrigatória") String senha,
    @NotNull(message = "Cargo ID é obrigatório") Long cargoId,
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos") String cpf
) {
    public CadastrarFuncionarioUseCase.Input toCadastrarInput() {
        return new CadastrarFuncionarioUseCase.Input(
                nome,
                username,
                senha,
                cargoId,
                cpf
        );
    }
}