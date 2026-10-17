package br.com.lata.velha.ordem_servico.domain.entities;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * TODO: Peça necessária para a execução de um serviço, do ponto de vista da OS: só o que é
 * preciso para diagnóstico/execução (id da peça, quantidade). Preço, estoque e reserva
 * são responsabilidade exclusiva do InventoryService; quem precisa do valor (o
 * BillingService, ao montar o orçamento após o evento DiagnosticoFinalizado) consulta o
 * InventoryService diretamente - ver billing-service-todo.md e general-flow.md passos
 * 4-14.
 */
public final class PecaNecessaria {
    private final Long id;
    private final Long pecaId;
    private final Long execucaoServicoId;

    private Integer quantidade;

    private LocalDateTime atualizado;

    public PecaNecessaria(Long id, Long pecaId, Long execucaoServicoId, Integer quantidade, LocalDateTime atualizado) {
        validatePecaId(pecaId);
        validateExecucaoServicoId(execucaoServicoId);

        this.id = id;
        this.pecaId = pecaId;
        this.execucaoServicoId = execucaoServicoId;
        setQuantidade(quantidade);
        this.atualizado = atualizado;
    }

    public static PecaNecessaria create(Long pecaId, Long execucaoServicoId, Integer quantidade) {
        return new PecaNecessaria(null, pecaId, execucaoServicoId, quantidade, LocalDateTime.now());
    }

    /* ================= GETTERS ================= */

    public Long getId() { return id; }
    public Long getPecaId() { return pecaId; }
    public Long getExecucaoServicoId() { return execucaoServicoId; }
    public Integer getQuantidade() { return quantidade; }
    public LocalDateTime getAtualizado() { return atualizado; }

    /* ================= OBJECT ================= */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PecaNecessaria)) return false;
        PecaNecessaria that = (PecaNecessaria) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        if (id == null)
            return Objects.hash(getPecaId(), getExecucaoServicoId());
        return Objects.hash(getId());
    }

    private void validateExecucaoServicoId(Long execucaoServicoId) {
        if (execucaoServicoId == null)
            throw new IllegalArgumentException("Serviço obrigatório");
    }

    private void validatePecaId(Long pecaId) {
        if (pecaId == null)
            throw new IllegalArgumentException("Peça obrigatória");
    }

    private void setQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade <= 0)
            throw new IllegalArgumentException("Quantidade inválida");
        this.quantidade = quantidade;
    }
}
