package br.com.lata.velha.ordem_servico.infrastructure.persistence.entities;

import br.com.lata.velha.ordem_servico.domain.entities.HistoricoEstadoOs;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_estado_os")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoEstadoOsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "os_id", nullable = false)
    private Long osId;

    @Column(name = "estado_os", nullable = false, length = 50)
    private String estadoOs;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    public static HistoricoEstadoOsEntity fromDomain(HistoricoEstadoOs domain) {
        return new HistoricoEstadoOsEntity(
                domain.getId(),
                domain.getOsId(),
                domain.getEstadoOs(),
                domain.getDataInicio(),
                domain.getDataFim()
        );
    }

    public HistoricoEstadoOs toDomain() {
        return new HistoricoEstadoOs(id, osId, estadoOs, dataInicio, dataFim);
    }
}
