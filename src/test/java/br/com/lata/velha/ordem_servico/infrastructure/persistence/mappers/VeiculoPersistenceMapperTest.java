package br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.value_objects.Placa;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ProprietarioEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.VeiculoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VeiculoPersistenceMapperTest {

    private final VeiculoPersistenceMapper mapper = new VeiculoPersistenceMapper();

    @Test
    @DisplayName("deve retornar null ao converter entity nula para domain")
    void shouldReturnNullWhenEntityIsNull() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    @DisplayName("deve converter entity para domain corretamente")
    void shouldMapEntityToDomain() {
        ProprietarioEntity proprietario = new ProprietarioEntity();
        proprietario.setId(5L);

        VeiculoEntity entity = buildVeiculoEntity(10L, "ABC1234", true, proprietario);

        Veiculo result = mapper.toDomain(entity);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(5L, result.getProprietarioId());
        assertEquals("ABC1234", result.getPlaca().getValor());
        assertEquals("Toyota", result.getMarca());
        assertEquals("Corolla", result.getModelo());
        assertEquals(2020, result.getAno());
        assertEquals("Prata", result.getCor());
        assertTrue(result.isAtivo());
    }

    @Test
    @DisplayName("deve preservar ativo=false ao converter entity para domain")
    void shouldPreserveInactiveStatusWhenMappingToDomain() {
        ProprietarioEntity proprietario = new ProprietarioEntity();
        proprietario.setId(1L);

        assertFalse(mapper.toDomain(buildVeiculoEntity(1L, "ABC1234", false, proprietario)).isAtivo());
    }

    @Test
    @DisplayName("deve retornar null ao converter domain nulo para entity")
    void shouldReturnNullWhenDomainIsNull() {
        assertNull(mapper.toEntity(null, new ProprietarioEntity()));
    }

    @Test
    @DisplayName("deve converter domain para entity corretamente")
    void shouldMapDomainToEntity() {
        Veiculo domain = new Veiculo(10L, 5L, Placa.of("ABC1234"), "Toyota", "Corolla", 2020, "Prata");

        ProprietarioEntity proprietarioEntity = new ProprietarioEntity();
        proprietarioEntity.setId(5L);

        VeiculoEntity result = mapper.toEntity(domain, proprietarioEntity);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(proprietarioEntity, result.getProprietario());
        assertEquals("ABC1234", result.getPlaca());
        assertEquals("Toyota", result.getMarca());
        assertEquals("Corolla", result.getModelo());
        assertEquals(2020, result.getAno());
        assertEquals("Prata", result.getCor());
        assertTrue(result.isAtivo());
    }

    @Test
    @DisplayName("deve preservar ativo=false ao converter domain para entity")
    void shouldPreserveInactiveStatusWhenMappingToEntity() {
        Veiculo domain = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Toyota", "Corolla", 2020, "Prata");
        domain.setAtivo(false);

        assertFalse(mapper.toEntity(domain, new ProprietarioEntity()).isAtivo());
    }

    private VeiculoEntity buildVeiculoEntity(Long id, String placa, boolean ativo,
                                              ProprietarioEntity proprietario) {
        VeiculoEntity entity = new VeiculoEntity();
        entity.setId(id);
        entity.setProprietario(proprietario);
        entity.setPlaca(placa);
        entity.setMarca("Toyota");
        entity.setModelo("Corolla");
        entity.setAno(2020);
        entity.setCor("Prata");
        entity.setAtivo(ativo);
        return entity;
    }
}
