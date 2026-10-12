package br.com.lata.velha.ordem_servico.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PROPRIETARIO")
@Getter
@Setter
@NoArgsConstructor
public class ProprietarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "DOCUMENTO", nullable = false, unique = true)
    private String documento;

    @Column(name = "NUMERO_CELULAR", nullable = false)
    private String numeroCelular;

    @Embedded
    private EnderecoEmbeddable endereco;

    @Column(name = "ATIVO", nullable = false)
    private boolean ativo = true;

    @OneToMany(mappedBy = "proprietario", fetch = FetchType.LAZY)
    private List<VeiculoEntity> veiculos = new ArrayList<>();
}