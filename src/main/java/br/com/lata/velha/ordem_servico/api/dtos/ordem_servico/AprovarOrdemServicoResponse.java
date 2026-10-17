package br.com.lata.velha.ordem_servico.api.dtos.ordem_servico;

import java.util.List;

public record AprovarOrdemServicoResponse(
        Long idOs,
        String status,
        List<Servico> servicos
) {
    public record Servico(Long idServicoOs, String statusServico) {}
}
