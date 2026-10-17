package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class OrdemServico {
    private final Long id;
    private final Long proprietarioId;
    private final Long veiculoId;
    private final String reclamacaoProprietario;
    private StatusOrdemServico status;

    private final LocalDateTime criadoEm;
    private LocalDateTime iniciadoEm;
    private LocalDateTime finalizadoEm;
    private LocalDateTime entregueEm;
    private LocalDateTime atualizadoEm;

    private Long atendenteInicioId;
    private Long atendenteAprovacaoId;
    private Long mecanicoResponsavelId;

    private final List<ExecucaoServico> execucaoServicos;
    private final List<HistoricoEstadoOs> historicoEstados;

    public OrdemServico(Long id, Long proprietarioId, Long veiculoId, String reclamacaoProprietario, StatusOrdemServico status, LocalDateTime criadoEm, LocalDateTime iniciadoEm, LocalDateTime finalizadoEm, LocalDateTime entregueEm, LocalDateTime atualizadoEm, Long atendenteInicioId, Long mecanicoResponsavelId, List<ExecucaoServico> execucaoServicos) {
        this(id, proprietarioId, veiculoId, reclamacaoProprietario, status, criadoEm, iniciadoEm, finalizadoEm, entregueEm, atualizadoEm, atendenteInicioId, mecanicoResponsavelId, execucaoServicos, new ArrayList<>());
    }

    public OrdemServico(Long id, Long proprietarioId, Long veiculoId, String reclamacaoProprietario, StatusOrdemServico status, LocalDateTime criadoEm, LocalDateTime iniciadoEm, LocalDateTime finalizadoEm, LocalDateTime entregueEm, LocalDateTime atualizadoEm, Long atendenteInicioId, Long mecanicoResponsavelId, List<ExecucaoServico> execucaoServicos, List<HistoricoEstadoOs> historicoEstados) {
        this.id = id;
        this.proprietarioId = proprietarioId;
        this.veiculoId = veiculoId;
        this.reclamacaoProprietario = reclamacaoProprietario;
        this.status = status;
        this.criadoEm = criadoEm;
        this.iniciadoEm = iniciadoEm;
        this.finalizadoEm = finalizadoEm;
        this.entregueEm = entregueEm;
        this.atualizadoEm = atualizadoEm;
        this.atendenteInicioId = atendenteInicioId;
        this.mecanicoResponsavelId = mecanicoResponsavelId;
        this.execucaoServicos = execucaoServicos;
        this.historicoEstados = historicoEstados != null ? historicoEstados : new ArrayList<>();
    }

    public static OrdemServico create(Long proprietarioId, Long veiculoId, String reclamacaoProprietario, Long atendenteInicioId) {
        var ordemServico = new OrdemServico(null, proprietarioId, veiculoId, reclamacaoProprietario, StatusOrdemServico.RECEBIDA, LocalDateTime.now(), null, null, null, null, atendenteInicioId, null, new ArrayList<>());
        ordemServico.historicoEstados.add(HistoricoEstadoOs.criar(null, StatusOrdemServico.RECEBIDA.name(), ordemServico.criadoEm, null));
        return ordemServico;
    }

    /* ================== FLUXO ================== */

    public void iniciarDiagnostico(Long mecanicoId) {
        validarStatus(StatusOrdemServico.RECEBIDA);
        this.mecanicoResponsavelId = mecanicoId;
        mudarStatus(StatusOrdemServico.EM_DIAGNOSTICO);
        touch();
    }

    public void finalizarDiagnostico(Long mecanicoId) {
        validarStatus(StatusOrdemServico.EM_DIAGNOSTICO);
        if(this.execucaoServicos.isEmpty()) {
            this.mecanicoResponsavelId = mecanicoId;
            mudarStatus(StatusOrdemServico.FINALIZADA);
            this.finalizadoEm = LocalDateTime.now();
        } else {
            mudarStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);
        }
        touch();
    }

    public void aprovar(Long atendenteId) {
        validarStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);
        boolean temServicoNaoDecidido = execucaoServicos.stream()
                .anyMatch(ExecucaoServico::isPendente);
        if (temServicoNaoDecidido)
            throw new IllegalStateException("Todos os serviços devem estar com status APROVADO ou RECUSADO antes de aprovar a OS.");

        boolean nenhumAprovado = execucaoServicos.stream()
                .allMatch(ExecucaoServico::isRecusado);
        if (nenhumAprovado)
            throw new IllegalStateException("É necessário pelo menos um serviço aprovado para aprovar a OS.");
        this.atendenteAprovacaoId = atendenteId;
        mudarStatus(StatusOrdemServico.APROVADA);
        touch();
    }

    public void iniciarExecucao() {
        validarStatus(StatusOrdemServico.APROVADA);
        mudarStatus(StatusOrdemServico.EM_EXECUCAO);
        this.iniciadoEm = LocalDateTime.now();
        touch();
    }

    public void reprovar(Long atendenteId) {
        validarStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);
        this.atendenteAprovacaoId = atendenteId;
        mudarStatus(StatusOrdemServico.REPROVADA);
        this.finalizadoEm = LocalDateTime.now();
        touch();
    }

    public void finalizar(Long mecanicoId) {
        validarStatus(StatusOrdemServico.EM_EXECUCAO);
        boolean existeExecucaoNaoConcluida = execucaoServicos.stream()
                .anyMatch(s -> !s.isConcluido());
        if (existeExecucaoNaoConcluida)
            throw new IllegalStateException("Existem execuções de serviço não finalizadas para esta OS!");
        this.mecanicoResponsavelId = mecanicoId;
        mudarStatus(StatusOrdemServico.FINALIZADA);
        this.finalizadoEm = LocalDateTime.now();
        touch();
    }

    public void entregar(Long atendenteId) {
        if (!this.isFinalizada() && !this.isReprovada())
            throw new IllegalStateException("Esta Ordem de Serviço não foi Finalizada: " + this.getId());
        this.atendenteInicioId =atendenteId;
        mudarStatus(StatusOrdemServico.ENTREGUE);
        this.entregueEm = LocalDateTime.now();
        touch();
    }

    public void adicionarServico(ExecucaoServico servico) {
        if (servico == null)
            throw new IllegalArgumentException("Serviço inválido");

        if (!this.isEmDiagnostico() && !this.isRecebida())
            throw new IllegalStateException("Não é possível adicionar serviço");

        boolean jaExiste = execucaoServicos.stream()
                .anyMatch(s -> s.getServicoId()
                        .equals(servico.getServicoId()));

        if (jaExiste)
            throw new IllegalStateException("Serviço já adicionado");

        execucaoServicos.add(servico);
        touch();
    }

    public void iniciarExecucaoServico(Long execucaoId, Long mecanicoId) {
        if (!this.isEmAndamento())
            throw new IllegalStateException("Esta Ordem de Serviço não pode ter serviços iniciados: " + this.getId());
        var execucao = this.execucaoServicos.stream()
                .filter(e -> e.getId().equals(execucaoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado na OS: " + execucaoId));
        if (!this.isEmExecucao()) this.iniciarExecucao();
        execucao.iniciar(mecanicoId);
    }

    public void finalizarExecucaoServico(Long execucaoId, Long mecanicoId) {
        if (!this.isEmExecucao())
            throw new IllegalStateException("Esta Ordem de Serviço não está em execução: " + this.getId());

        var execucao = getExecucaoById(execucaoId);
        execucao.finalizar();

        if (this.todosServicosConcluidos())
            this.finalizar(mecanicoId);
    }

    public ExecucaoServico getExecucaoById(Long execucaoId) {
        return this.getExecucaoServicos().stream()
                .filter(e -> e.getId().equals(execucaoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado na OS: " + execucaoId));
    }

    public boolean isAprovada() {
        return this.status == StatusOrdemServico.APROVADA;
    }

    public boolean isEmDiagnostico() { return this.status == StatusOrdemServico.EM_DIAGNOSTICO; }

    public boolean isRecebida() { return this.status == StatusOrdemServico.RECEBIDA; }

    public boolean isEmExecucao() {
        return this.status == StatusOrdemServico.EM_EXECUCAO;
    }

    public boolean isEmAndamento() {
        return this.isAprovada() || this.isEmExecucao();
    }

    public boolean isReprovada() {
        return this.status.equals(StatusOrdemServico.REPROVADA);
    }

    public boolean isFinalizada() {
        return this.status.equals(StatusOrdemServico.FINALIZADA);
    }

    public boolean todosServicosConcluidos() {
        return this.execucaoServicos.stream()
                .allMatch(ExecucaoServico::isConcluido);
    }

    /* ================== HELPERS ================== */

    private void validarStatus(StatusOrdemServico esperado) {
        if (this.status != esperado) {
            throw new IllegalStateException(
                    "Status inválido. Atual: " + status +
                            " esperado: " + esperado
            );
        }
    }

    private void touch() {
        this.atualizadoEm = LocalDateTime.now();
    }

    private void mudarStatus(StatusOrdemServico novo) {
        if (this.status == novo) return;

        var agora = LocalDateTime.now();
        this.historicoEstados.stream()
                .filter(HistoricoEstadoOs::isAberto)
                .forEach(h -> h.fechar(agora));
        this.historicoEstados.add(HistoricoEstadoOs.criar(this.id, novo.name(), agora, null));
        this.status = novo;
    }

    /* ================== GETTERS ================== */

    public Long getId() { return id; }
    public Long getProprietarioId() { return proprietarioId; }
    public Long getVeiculoId() { return veiculoId; }
    public String getReclamacaoProprietario() { return reclamacaoProprietario; }
    public StatusOrdemServico getStatus() { return status; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getIniciadoEm() { return iniciadoEm; }
    public LocalDateTime getFinalizadoEm() { return finalizadoEm; }
    public LocalDateTime getEntregueEm() { return entregueEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public Long getAtendenteInicioId() { return atendenteInicioId; }
    public Long getAtendenteAprovacaoId() { return atendenteAprovacaoId; }
    public Long getMecanicoResponsavelId() { return mecanicoResponsavelId; }
    public List<ExecucaoServico> getExecucaoServicos() { return execucaoServicos; }
    public List<HistoricoEstadoOs> getHistoricoEstados() { return historicoEstados; }

    /* ================== OBJECT ================== */

    @Override
    public String toString() {
        return "OrdemServico{id=" + id +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrdemServico)) return false;
        OrdemServico that = (OrdemServico) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}