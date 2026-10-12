package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public final class ExecucaoServico {
    private final Long id;
    private final Long servicoId;
    private final Long ordemServicoId;
    private StatusExecucaoServico status;

    private BigDecimal valorMaoDeObra;
    private Set<PecaNecessaria> pecasNecessarias;

    private Long atendenteAprovacaoId;
    private Long mecanicoResponsavelId;

    private LocalDateTime iniciadoEm;
    private LocalDateTime terminadoEm;
    private LocalDateTime atualizadoEm;

    public ExecucaoServico(Long id, Long servicoId, Long ordemServicoId, StatusExecucaoServico status, BigDecimal valorMaoDeObra, Set<PecaNecessaria> pecasNecessarias, Long atendenteAprovacaoId, Long mecanicoResponsavelId, LocalDateTime iniciadoEm, LocalDateTime terminadoEm, LocalDateTime atualizadoEm) {
        validateServicoId(servicoId);
        validateOrdemServicoId(ordemServicoId);
        validateMaodeObra(valorMaoDeObra);
        this.id = id;
        this.status = status;
        this.servicoId = servicoId;
        this.ordemServicoId = ordemServicoId;
        this.atendenteAprovacaoId = atendenteAprovacaoId;
        this.mecanicoResponsavelId = mecanicoResponsavelId;
        this.valorMaoDeObra = valorMaoDeObra;
        this.pecasNecessarias = pecasNecessarias;
        this.iniciadoEm = iniciadoEm;
        this.terminadoEm = terminadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static ExecucaoServico create(Long servicoId, Long ordemServicoId, BigDecimal valorMaoDeObra) {
        var statusInicial = StatusExecucaoServico.PENDENTE;
        var atualizadoEm = LocalDateTime.now();
        return new ExecucaoServico(null, servicoId, ordemServicoId, statusInicial, valorMaoDeObra, new HashSet<>(), null, null, null, null, atualizadoEm);
    }

    public void aprovar(Long atendenteId) {
        if (this.status != StatusExecucaoServico.PENDENTE)
            throw new IllegalStateException("Só serviços pendentes podem ser aprovados");
        this.status = statusAprovacao();
        this.atendenteAprovacaoId = atendenteId;
        touch();
    }

    public void recusar(Long atendenteId) {
        if (status != StatusExecucaoServico.PENDENTE)
            throw new IllegalStateException("Só serviços pendentes podem ser aprovados");
        this.status = StatusExecucaoServico.RECUSADO;
        this.atendenteAprovacaoId = atendenteId;
        this.terminadoEm = LocalDateTime.now();
        touch();
    }

    public void iniciar(Long mecanicoResponsavelId) {
        if (!this.isAprovado())
            throw new IllegalStateException("Só serviços aprovados podem ser iniciados");
        // TODO: gate de "peças faltantes" deve vir de um futuro InventoryChecked
        // (general-flow.md passo 8-10) em vez de estado local - InventoryService
        // é quem sabe se há peça em falta.
        this.status = StatusExecucaoServico.EM_EXECUCAO;
        this.mecanicoResponsavelId = mecanicoResponsavelId;
        this.iniciadoEm = LocalDateTime.now();
        touch();
    }

    public void finalizar() {
        if (status != StatusExecucaoServico.EM_EXECUCAO)
            throw new IllegalStateException("Não é possível finalizar um serviço que não está em execução");
        // TODO: baixa/instalação de peças passa a ser responsabilidade exclusiva do
        // InventoryService via evento ServiceEnded (general-flow.md passo 13-14).
        this.status = StatusExecucaoServico.FINALIZADO;
        this.terminadoEm = LocalDateTime.now();
        touch();
    }

    public void adicionarPecaNecessaria(PecaNecessaria peca) {
        if (status == StatusExecucaoServico.FINALIZADO)
            throw new IllegalStateException("Serviço já finalizado");
        if (peca == null)
            throw new IllegalArgumentException("Peça inválida");
        var pecaDuplicada = pecasNecessarias.stream()
                .anyMatch(p -> p.getPecaId().equals(peca.getPecaId()));
        if (pecaDuplicada)
            throw new IllegalStateException("Peça já adicionada ao serviço");
        pecasNecessarias.add(peca);
        touch();
    }

    public boolean isPendente() {
        return this.status.equals(StatusExecucaoServico.PENDENTE);
    }

    public boolean isAprovado() {
        return this.status.equals(StatusExecucaoServico.APROVADO);
    }

    public boolean isRecusado() {
        return this.status.equals(StatusExecucaoServico.RECUSADO);
    }

    public boolean isFinalizado() {
        return this.status.equals(StatusExecucaoServico.FINALIZADO);
    }

    public boolean isConcluido() {
        return this.isFinalizado() || this.isRecusado();
    }

    public Long getId() {
        return id;
    }

    public StatusExecucaoServico getStatus() {
        return status;
    }

    public Long getOrdemServicoId() {
        return ordemServicoId;
    }

    public Long getServicoId() {
        return servicoId;
    }

    public Long getAtendenteAprovacaoId() {
        return atendenteAprovacaoId;
    }

    public Long getMecanicoResponsavelId() {
        return mecanicoResponsavelId;
    }

    public LocalDateTime getIniciadoEm() {
        return iniciadoEm;
    }

    public LocalDateTime getTerminadoEm() {
        return terminadoEm;
    }

    public Set<PecaNecessaria> getPecasNecessarias() {
        return pecasNecessarias;
    }

    public BigDecimal getValorMaoDeObra() {
        return valorMaoDeObra;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    // TODO remove this setter
    public void setStatus(StatusExecucaoServico status) {
        this.status = status;
    }

    private void touch() {
        this.atualizadoEm = LocalDateTime.now();
    }

    private void validateServicoId(Long servico) {
        if (servico == null)
            throw new IllegalArgumentException("Serviço obrigatório");
    }

    private void validateOrdemServicoId(Long ordemServicoId) {
        if(ordemServicoId == null)
            throw new IllegalArgumentException("Ordem de serviço é obrigatória");
    }

    private void validateMaodeObra(BigDecimal valorMaoDeObra) {
        if (valorMaoDeObra == null || valorMaoDeObra.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor inválido");
        }
    }

    private StatusExecucaoServico statusAprovacao() {
        // TODO: o estado AGUARDANDO_PECA deve ser definido por um futuro handler do
        // evento InventoryChecked (general-flow.md passo 8-10), não mais localmente.
        return StatusExecucaoServico.APROVADO;
    }
}