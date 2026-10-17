package br.com.lata.velha.ordem_servico.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class EnderecoEmbeddable {

    @Column(name = "RUA")
    private String rua;

    @Column(name = "CEP")
    private String cep;

    @Column(name = "NUMERO_CASA")
    private String numeroCasa;
}