package br.com.lata.velha.ordem_servico.infrastructure.persistence.entities;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FuncionarioEntityTest {

    @Test
    void deveSetarEObterCampos() {
        FuncionarioEntity entity = new FuncionarioEntity();
        CargoEntity cargo = new CargoEntity();
        UUID userId = UUID.randomUUID();

        cargo.setId(1L);
        cargo.setNome("MECANICO");

        entity.setId(2L);
        entity.setNome("Carlos");
        entity.setCargo(cargo);
        entity.setUserId(userId);

        assertThat(entity.getId()).isEqualTo(2L);
        assertThat(entity.getNome()).isEqualTo("Carlos");
        assertThat(entity.getCargo().getNome()).isEqualTo("MECANICO");
        assertThat(entity.getUserId()).isEqualTo(userId);
        assertThat(entity.toString()).contains("Carlos");
    }

    @Test
    void deveAplicarEqualsEHashCodeComMesmoConteudo() {
        UUID userId = UUID.randomUUID();
        CargoEntity cargo = new CargoEntity();
        cargo.setId(1L);
        cargo.setNome("ADMIN");

        FuncionarioEntity a = new FuncionarioEntity();
        a.setId(10L);
        a.setNome("Ana");
        a.setCargo(cargo);
        a.setUserId(userId);

        FuncionarioEntity b = new FuncionarioEntity();
        b.setId(10L);
        b.setNome("Ana");
        b.setCargo(cargo);
        b.setUserId(userId);

        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
    }
}
