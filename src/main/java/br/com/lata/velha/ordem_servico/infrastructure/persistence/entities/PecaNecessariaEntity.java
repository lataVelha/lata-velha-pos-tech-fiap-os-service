package br.com.lata.velha.ordem_servico.infrastructure.persistence.entities;

import br.com.lata.velha.ordem_servico.domain.entities.PecaNecessaria;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "PECA_NECESSARIA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PecaNecessariaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PECA_ID", nullable = false)
    private Long pecaId;

    @Column(name = "EXECUCAO_SERVICO_ID", nullable = false)
    private Long execucaoServicoId;

    @Column(name = "QUANTIDADE", nullable = false)
    private Integer quantidade;

    @Column(name = "ATUALIZADO_EM")
    private LocalDateTime atualizado;

    public static PecaNecessariaEntity fromDomain(PecaNecessaria domain) {
        if (domain == null) return null;
        return new PecaNecessariaEntity(
                domain.getId(),
                domain.getPecaId(),
                domain.getExecucaoServicoId(),
                domain.getQuantidade(),
                domain.getAtualizado()
        );
    }

    public PecaNecessaria toDomain() {
        return new PecaNecessaria(
                id,
                pecaId,
                execucaoServicoId,
                quantidade,
                atualizado
        );
    }

    public void setExecucaoServico(ExecucaoServicoEntity execucaoServico) {
        this.execucaoServicoId = execucaoServico != null ? execucaoServico.getId() : null;
    }

    public ExecucaoServicoEntity getExecucaoServico() {
        if (this.execucaoServicoId == null) {
            return null;
        }
        ExecucaoServicoEntity execucaoServico = new ExecucaoServicoEntity();
        execucaoServico.setId(this.execucaoServicoId);
        return execucaoServico;
    }
}
