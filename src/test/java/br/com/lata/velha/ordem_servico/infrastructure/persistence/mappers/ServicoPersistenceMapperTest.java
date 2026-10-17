package br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers;

import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ServicoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServicoPersistenceMapperTest {

    private ServicoPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ServicoPersistenceMapper();
    }

    // ------------------------------------------------------------------ toDomain

    @Test
    @DisplayName("toDomain deve retornar null quando entity é null")
    void toDomain_deveRetornarNullQuandoEntityEhNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("toDomain deve mapear todos os campos corretamente")
    void toDomain_deveMapearTodosCampos() {
        var entity = new ServicoEntity();
        entity.setId(10L);
        entity.setNome("Troca de óleo");
        entity.setDescricao("Troca do óleo do motor");
        entity.setAtivo(true);

        var domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(10L);
        assertThat(domain.getNome()).isEqualTo("Troca de óleo");
        assertThat(domain.getDescricao()).isEqualTo("Troca do óleo do motor");
        assertThat(domain.isAtivo()).isTrue();
    }

    @Test
    @DisplayName("toDomain deve mapear ativo false corretamente")
    void toDomain_deveMapearAtivoFalse() {
        var entity = new ServicoEntity();
        entity.setId(11L);
        entity.setNome("Alinhamento");
        entity.setDescricao("Alinhamento de rodas");
        entity.setAtivo(false);

        assertThat(mapper.toDomain(entity).isAtivo()).isFalse();
    }

    // ------------------------------------------------------------------ toEntity

    @Test
    @DisplayName("toEntity deve retornar null quando domain é null")
    void toEntity_deveRetornarNullQuandoDomainEhNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("toEntity deve mapear todos os campos corretamente")
    void toEntity_deveMapearTodosCampos() {
        var domain = new Servico(5L, "Balanceamento", "Balanceamento de rodas", true);

        var entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(5L);
        assertThat(entity.getNome()).isEqualTo("Balanceamento");
        assertThat(entity.getDescricao()).isEqualTo("Balanceamento de rodas");
        assertThat(entity.isAtivo()).isTrue();
    }

    @Test
    @DisplayName("toEntity deve mapear ativo false corretamente")
    void toEntity_deveMapearAtivoFalse() {
        var domain = new Servico(6L, "Antigo", "Serviço desativado", false);

        assertThat(mapper.toEntity(domain).isAtivo()).isFalse();
    }

    @Test
    @DisplayName("toEntity deve mapear id null para novo serviço")
    void toEntity_deveMapearIdNullParaNovoServico() {
        var domain = new Servico(null, "Novo", "Novo serviço", true);

        var entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getNome()).isEqualTo("Novo");
    }

    @Test
    @DisplayName("roundTrip domain→entity→domain deve preservar dados")
    void roundTrip_devePreservarDados() {
        var original = new Servico(7L, "Revisão", "Revisão completa", true);

        var roundTrip = mapper.toDomain(mapper.toEntity(original));

        assertThat(roundTrip.getId()).isEqualTo(original.getId());
        assertThat(roundTrip.getNome()).isEqualTo(original.getNome());
        assertThat(roundTrip.getDescricao()).isEqualTo(original.getDescricao());
        assertThat(roundTrip.isAtivo()).isEqualTo(original.isAtivo());
    }
}
