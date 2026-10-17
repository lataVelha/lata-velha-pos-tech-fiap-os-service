package br.com.lata.velha.ordem_servico.api.presenters.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.TempoMedioExecucaoResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.TempoMedioExecucaoServicoItemResponse;
import br.com.lata.velha.ordem_servico.application.presenters.ordemservico.BuscarTempoMedioExecucaoPresenter;
import br.com.lata.velha.ordem_servico.domain.entities.TempoMedioExecucaoPorServico;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Component
public class BuscarTempoMedioExecucaoPresenterImpl implements BuscarTempoMedioExecucaoPresenter {

    private static final String TIMEZONE = "America/Sao_Paulo";

    @Override
    public TempoMedioExecucaoResponse present(List<TempoMedioExecucaoPorServico> itens, LocalDate dataInicio, LocalDate dataFim) {
        List<TempoMedioExecucaoServicoItemResponse> servicos = itens.stream()
                .map(item -> new TempoMedioExecucaoServicoItemResponse(
                        item.servicoId(),
                        item.servicoNome(),
                        BigDecimal.valueOf(item.tempoMedioMinutos() == null ? 0.0 : item.tempoMedioMinutos())
                                .setScale(2, RoundingMode.HALF_UP)
                ))
                .toList();
        return new TempoMedioExecucaoResponse(servicos, dataInicio, dataFim, TIMEZONE);
    }
}
