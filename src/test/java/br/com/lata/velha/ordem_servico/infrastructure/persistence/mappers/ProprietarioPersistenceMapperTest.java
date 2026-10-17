package br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.value_objects.Documento;
import br.com.lata.velha.ordem_servico.domain.value_objects.Endereco;
import br.com.lata.velha.ordem_servico.domain.value_objects.NumeroCelular;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.EnderecoEmbeddable;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ProprietarioEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.VeiculoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProprietarioPersistenceMapperTest {

    private final ProprietarioPersistenceMapper mapper = new ProprietarioPersistenceMapper();

    @Test
    @DisplayName("deve retornar null ao converter entity nula para domain")
    void shouldReturnNullWhenEntityIsNull() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    @DisplayName("deve converter entity para domain corretamente")
    void shouldMapEntityToDomain() {
        ProprietarioEntity entity = buildEntity(1L, true, null);

        Proprietario result = mapper.toDomain(entity);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("João Silva", result.getNome());
        assertEquals("joao@email.com", result.getEmail());
        assertEquals("52998224725", result.getDocumento().getValor());
        assertEquals("11999990001", result.getNumeroCelular().getValor());
        assertTrue(result.isAtivo());
        assertNull(result.getEndereco());
        assertTrue(result.getVeiculos().isEmpty());
    }

    @Test
    @DisplayName("deve converter entity com endereço para domain")
    void shouldMapEntityWithEndereceToDomain() {
        EnderecoEmbeddable end = new EnderecoEmbeddable();
        end.setRua("Rua das Flores");
        end.setCep("01001000");
        end.setNumeroCasa("123");

        Proprietario result = mapper.toDomain(buildEntity(2L, true, end));

        assertNotNull(result.getEndereco());
        assertEquals("Rua das Flores", result.getEndereco().getRua());
        assertEquals("01001000", result.getEndereco().getCep());
        assertEquals("123", result.getEndereco().getNumeroCasa());
    }

    @Test
    @DisplayName("deve converter entity com veículos para domain")
    void shouldMapEntityWithVeiculosToDomain() {
        ProprietarioEntity entity = buildEntity(1L, true, null);

        VeiculoEntity veiculo = new VeiculoEntity();
        veiculo.setId(10L);
        veiculo.setProprietario(entity);
        veiculo.setPlaca("ABC1234");
        veiculo.setMarca("Toyota");
        veiculo.setModelo("Corolla");
        veiculo.setAno(2020);
        veiculo.setCor("Prata");
        veiculo.setAtivo(true);
        entity.setVeiculos(List.of(veiculo));

        Proprietario result = mapper.toDomain(entity);

        assertEquals(1, result.getVeiculos().size());
        assertEquals(10L, result.getVeiculos().get(0).getId());
        assertEquals("ABC1234", result.getVeiculos().get(0).getPlaca().getValor());
    }

    @Test
    @DisplayName("deve preservar ativo=false ao converter entity para domain")
    void shouldPreserveInativeStatusWhenMappingToDomain() {
        assertFalse(mapper.toDomain(buildEntity(3L, false, null)).isAtivo());
    }

    @Test
    @DisplayName("deve retornar null ao converter domain nulo para entity")
    void shouldReturnNullWhenDomainIsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("deve converter domain para entity corretamente")
    void shouldMapDomainToEntity() {
        Proprietario domain = new Proprietario(1L, "João Silva", "joao@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);

        ProprietarioEntity result = mapper.toEntity(domain);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("João Silva", result.getNome());
        assertEquals("joao@email.com", result.getEmail());
        assertEquals("52998224725", result.getDocumento());
        assertEquals("11999990001", result.getNumeroCelular());
        assertTrue(result.isAtivo());
        assertNull(result.getEndereco());
    }

    @Test
    @DisplayName("deve converter domain com endereço para entity")
    void shouldMapDomainWithEnderecoToEntity() {
        Endereco endereco = new Endereco("Rua A", "01001000", "10");
        Proprietario domain = new Proprietario(2L, "Ana", "ana@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), endereco);

        ProprietarioEntity result = mapper.toEntity(domain);

        assertNotNull(result.getEndereco());
        assertEquals("Rua A", result.getEndereco().getRua());
        assertEquals("01001000", result.getEndereco().getCep());
        assertEquals("10", result.getEndereco().getNumeroCasa());
    }

    @Test
    @DisplayName("deve preservar ativo=false ao converter domain para entity")
    void shouldPreserveInactiveStatusWhenMappingToEntity() {
        Proprietario domain = new Proprietario(3L, "Luis", "luis@email.com",
                Documento.of("52998224725"), NumeroCelular.of("11999990001"), null);
        domain.setAtivo(false);

        assertFalse(mapper.toEntity(domain).isAtivo());
    }

    private ProprietarioEntity buildEntity(Long id, boolean ativo, EnderecoEmbeddable end) {
        ProprietarioEntity entity = new ProprietarioEntity();
        entity.setId(id);
        entity.setNome("João Silva");
        entity.setEmail("joao@email.com");
        entity.setDocumento("52998224725");
        entity.setNumeroCelular("11999990001");
        entity.setAtivo(ativo);
        entity.setEndereco(end);
        entity.setVeiculos(List.of());
        return entity;
    }
}
