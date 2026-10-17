package br.com.lata.velha.ordem_servico.application.dtos.request;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.value_objects.Documento;
import br.com.lata.velha.ordem_servico.domain.value_objects.Endereco;
import br.com.lata.velha.ordem_servico.domain.value_objects.NumeroCelular;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para criação/atualização de proprietário")
public record ProprietarioRequest(
        @NotBlank(message = "Nome é obrigatório") @Schema(example = "João da Silva") String nome,
        @NotBlank(message = "Email é obrigatório") @Email(message = "Email inválido") @Schema(example = "joao@email.com") String email,
        @NotBlank(message = "Documento é obrigatório") @Schema(description = "CPF ou CNPJ", example = "123.456.789-00") String documento,
        @NotBlank(message = "Número de celular é obrigatório") @Schema(example = "(11) 99999-9999") String numeroCelular,
        @Valid @Schema(description = "Endereço do proprietário") EnderecoRequest endereco
) {
    public Proprietario toDomain() {
        Endereco end = endereco != null
                ? new Endereco(endereco.rua(), endereco.cep(), endereco.numeroCasa())
                : null;
        return new Proprietario(null, nome, email, Documento.of(documento), NumeroCelular.of(numeroCelular), end);
    }

    public void updateDomain(Proprietario existente) {
        existente.setNome(nome);
        existente.setEmail(email);
        existente.setDocumento(Documento.of(documento));
        existente.setNumeroCelular(NumeroCelular.of(numeroCelular));
        if (endereco != null) {
            existente.setEndereco(new Endereco(endereco.rua(), endereco.cep(), endereco.numeroCasa()));
        } else {
            existente.setEndereco(null);
        }
    }
}
