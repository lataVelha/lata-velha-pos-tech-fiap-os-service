package br.com.lata.velha.ordem_servico.infrastructure.persistence.entities;

import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "SERVICO")
@Data
public class ServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "DESCRICAO", nullable = false)
    private String descricao;

    @Column(name = "ATIVO", nullable = false)
    private boolean ativo = true;

    public Servico toDomain() {
        return new Servico(id, nome, descricao, ativo);
    }
}