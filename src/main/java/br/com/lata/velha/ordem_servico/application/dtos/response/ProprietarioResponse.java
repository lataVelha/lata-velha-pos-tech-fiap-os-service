package br.com.lata.velha.ordem_servico.application.dtos.response;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Dados do proprietário")
public record ProprietarioResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "João da Silva") String nome,
        @Schema(example = "joao@email.com") String email,
        @Schema(example = "123.456.789-00") String documento,
        @Schema(example = "(11) 99999-9999") String numeroCelular,
        EnderecoResponse endereco,
        List<VeiculoResponse> veiculos
) {
    public static ProprietarioResponse from(Proprietario p) {
        EnderecoResponse endResp = EnderecoResponse.from(p.getEndereco());
        List<VeiculoResponse> veicResp = p.getVeiculos() == null
                ? List.of()
                : p.getVeiculos().stream().map(VeiculoResponse::from).toList();
        return new ProprietarioResponse(
                p.getId(),
                p.getNome(),
                p.getEmail(),
                p.getDocumento().getFormatted(),
                p.getNumeroCelular().getFormatted(),
                endResp,
                veicResp
        );
    }
}
