package br.com.lata.velha.ordem_servico.infrastructure.persistence.entities;
 
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "FUNCIONARIO")
@Data
public class FuncionarioEntity {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
 
    @Column(name = "NOME", nullable = false)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARGO_ID", nullable = false)
    private CargoEntity cargo;

    @Column(name = "USER_ID", nullable = false)
    private UUID userId;
}
 