package br.com.lata.velha.ordem_servico.domain.entities;

import java.time.LocalDateTime;

public class HistoricoEstadoOs {
    private Long id;
    private Long osId;
    private String estadoOs;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    public HistoricoEstadoOs(Long id, Long osId, String estadoOs, LocalDateTime dataInicio, LocalDateTime dataFim) {
        this.id = id;
        this.osId = osId;
        this.estadoOs = estadoOs;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public static HistoricoEstadoOs criar(Long osId, String estadoOs, LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (estadoOs == null) throw new IllegalArgumentException("O estado é obrigatório.");
        if (dataInicio == null) throw new IllegalArgumentException("A data de início é obrigatória.");

        return new HistoricoEstadoOs(null, osId, estadoOs, dataInicio, dataFim);
    }

    public void fechar(LocalDateTime dataFim) {
        if (this.dataFim == null) {
            this.dataFim = dataFim;
        }
    }

    public boolean isAberto() {
        return this.dataFim == null;
    }

    public Long getId() { return id; }
    public Long getOsId() { return osId; }
    public String getEstadoOs() { return estadoOs; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
}
