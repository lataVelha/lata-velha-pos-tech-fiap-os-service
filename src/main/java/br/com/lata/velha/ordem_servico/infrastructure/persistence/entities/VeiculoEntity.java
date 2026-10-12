package br.com.lata.velha.ordem_servico.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "VEICULO")
@Getter
@Setter
@NoArgsConstructor
public class VeiculoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROPRIETARIO_ID", nullable = false)
    private ProprietarioEntity proprietario;

    @Column(name = "PLACA", nullable = false, unique = true)
    private String placa;

    @Column(name = "MARCA", nullable = false)
    private String marca;

    @Column(name = "MODELO", nullable = false)
    private String modelo;

    @Column(name = "ANO", nullable = false)
    private Integer ano;

    @Column(name = "COR", nullable = false)
    private String cor;

    @Column(name = "ATIVO", nullable = false)
    private boolean ativo = true;
}