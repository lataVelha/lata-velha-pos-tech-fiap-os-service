package br.com.lata.velha.ordem_servico.infrastructure.persistence.entities;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProprietarioEntityTest {

    @Test
    void deveInicializarComDefaults() {
        ProprietarioEntity entity = new ProprietarioEntity();

        assertThat(entity.isAtivo()).isTrue();
        assertThat(entity.getVeiculos()).isNotNull();
        assertThat(entity.getVeiculos()).isEmpty();
    }

    @Test
    void devePermitirSetarCamposBasicos() {
        ProprietarioEntity entity = new ProprietarioEntity();
        EnderecoEmbeddable endereco = new EnderecoEmbeddable();
        endereco.setRua("Rua A");
        endereco.setCep("01001000");
        endereco.setNumeroCasa("10");

        VeiculoEntity veiculo = new VeiculoEntity();
        veiculo.setId(9L);

        entity.setId(1L);
        entity.setNome("Maria");
        entity.setEmail("maria@email.com");
        entity.setDocumento("11122233344");
        entity.setNumeroCelular("11999999999");
        entity.setEndereco(endereco);
        entity.setAtivo(false);
        entity.setVeiculos(List.of(veiculo));

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getNome()).isEqualTo("Maria");
        assertThat(entity.getEmail()).isEqualTo("maria@email.com");
        assertThat(entity.getDocumento()).isEqualTo("11122233344");
        assertThat(entity.getNumeroCelular()).isEqualTo("11999999999");
        assertThat(entity.getEndereco().getRua()).isEqualTo("Rua A");
        assertThat(entity.isAtivo()).isFalse();
        assertThat(entity.getVeiculos()).hasSize(1);
    }
}
