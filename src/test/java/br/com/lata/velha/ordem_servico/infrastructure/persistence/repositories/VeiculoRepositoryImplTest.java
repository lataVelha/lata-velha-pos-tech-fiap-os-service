package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.ProprietarioNotFoundException;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.VeiculoNotFoundException;
import br.com.lata.velha.ordem_servico.domain.value_objects.Documento;
import br.com.lata.velha.ordem_servico.domain.value_objects.NumeroCelular;
import br.com.lata.velha.ordem_servico.domain.value_objects.Placa;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.ProprietarioPersistenceMapper;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.VeiculoPersistenceMapper;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.shared.infrastructure.logging.Slf4jLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({VeiculoRepositoryImpl.class, VeiculoPersistenceMapper.class,
        ProprietarioRepositoryImpl.class, ProprietarioPersistenceMapper.class, Slf4jLogger.class})
class VeiculoRepositoryImplTest {

    @Autowired
    private VeiculoRepositoryImpl repository;

    @Autowired
    private ProprietarioRepositoryImpl proprietarioRepository;

    private Proprietario proprietarioSalvo;

    @BeforeEach
    void setUp() {
        Proprietario proprietario = new Proprietario(null, "Maria Souza", "maria@email.com",
                Documento.of("11144477735"), NumeroCelular.of("11987654321"), null);
        proprietarioSalvo = proprietarioRepository.save(proprietario);
    }

    @Test
    @DisplayName("deve salvar novo veículo com sucesso")
    void shouldSaveNewVeiculo() {
        Veiculo veiculo = buildVeiculo(null, "ABC1234");

        Veiculo saved = repository.save(veiculo);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPlaca().getValor()).isEqualTo("ABC1234");
        assertThat(saved.getMarca()).isEqualTo("Toyota");
        assertThat(saved.getModelo()).isEqualTo("Corolla");
        assertThat(saved.getAno()).isEqualTo(2020);
        assertThat(saved.getCor()).isEqualTo("Prata");
        assertThat(saved.getProprietarioId()).isEqualTo(proprietarioSalvo.getId());
        assertThat(saved.isAtivo()).isTrue();
    }

    @Test
    @DisplayName("deve lançar exceção ao salvar veículo com placa duplicada")
    void shouldThrowWhenPlacaDuplicated() {
        repository.save(buildVeiculo(null, "ABC1234"));
        var veiculo = buildVeiculo(null, "ABC1234");

        assertThatThrownBy(() -> repository.save(veiculo))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    @DisplayName("deve lançar exceção ao salvar veículo com proprietário inexistente")
    void shouldThrowWhenProprietarioNotFound() {
        Veiculo veiculo = new Veiculo(null, 999L, Placa.of("XYZ9876"), "Honda", "Civic", 2022, "Azul");

        assertThatThrownBy(() -> repository.save(veiculo))
                .isInstanceOf(ProprietarioNotFoundException.class);
    }

    @Test
    @DisplayName("deve atualizar veículo existente sem validar placa duplicada")
    void shouldUpdateExistingVeiculoWithoutPlacaCheck() {
        Veiculo saved = repository.save(buildVeiculo(null, "ABC1234"));
        saved.setCor("Vermelho");

        Veiculo updated = repository.save(saved);

        assertThat(updated.getCor()).isEqualTo("Vermelho");
        assertThat(updated.getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("deve encontrar veículo ativo por id")
    void shouldGetActiveById() {
        Veiculo saved = repository.save(buildVeiculo(null, "ABC1234"));

        Veiculo found = repository.getActiveById(saved.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.isAtivo()).isTrue();
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar veículo ativo por id inexistente")
    void shouldThrowWhenGetActiveByIdNotFound() {
        assertThatThrownBy(() -> repository.getActiveById(999L))
                .isInstanceOf(VeiculoNotFoundException.class);
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar por id de veículo inativo")
    void shouldThrowWhenGetActiveByIdButVeiculoIsInactive() {
        Veiculo saved = repository.save(buildVeiculo(null, "ABC1234"));
        saved.deactivate();
        repository.save(saved);
        var id = saved.getId();

        assertThatThrownBy(() -> repository.getActiveById(id))
                .isInstanceOf(VeiculoNotFoundException.class);
    }

    @Test
    @DisplayName("deve encontrar veículos ativos por proprietário")
    void shouldFindActiveByProprietarioId() {
        repository.save(buildVeiculo(null, "ABC1234"));
        repository.save(buildVeiculo(null, "DEF5678"));

        List<Veiculo> veiculos = repository.findActiveByProprietarioId(proprietarioSalvo.getId());

        assertThat(veiculos).hasSize(2)
                .allMatch(Veiculo::isAtivo);
    }

    @Test
    @DisplayName("deve retornar lista vazia quando proprietário não tem veículos ativos")
    void shouldReturnEmptyListWhenNoActiveVeiculos() {
        List<Veiculo> veiculos = repository.findActiveByProprietarioId(proprietarioSalvo.getId());

        assertThat(veiculos).isEmpty();
    }

    @Test
    @DisplayName("deve listar apenas veículos ativos")
    void shouldFindAllActive() {
        repository.save(buildVeiculo(null, "ABC1234"));

        Veiculo inativo = repository.save(buildVeiculo(null, "DEF5678"));
        inativo.deactivate();
        repository.save(inativo);

        List<Veiculo> ativos = repository.findAllActive();

        assertThat(ativos).isNotEmpty()
                .allMatch(Veiculo::isAtivo);
    }

    @Test
    @DisplayName("deve retornar resultado paginado de veículos ativos")
    void shouldFindAllActivePaginated() {
        repository.save(buildVeiculo(null, "ABC1234"));
        repository.save(buildVeiculo(null, "DEF5678"));

        PaginatedResult<Veiculo> result = repository.findAllActivePaginated(0, 10);

        assertThat(result.content()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isGreaterThanOrEqualTo(2);
        assertThat(result.totalPages()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("deve encontrar veículo inativo por id")
    void shouldFindInactiveById() {
        Veiculo saved = repository.save(buildVeiculo(null, "ABC1234"));
        saved.deactivate();
        repository.save(saved);

        Veiculo found = repository.findInactiveById(saved.getId());

        assertThat(found).isNotNull();
        assertThat(found.isAtivo()).isFalse();
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar veículo inativo por id inexistente")
    void shouldThrowWhenFindInactiveByIdNotFound() {
        assertThatThrownBy(() -> repository.findInactiveById(999L))
                .isInstanceOf(VeiculoNotFoundException.class);
    }

    private Veiculo buildVeiculo(Long id, String placa) {
        return new Veiculo(id, proprietarioSalvo.getId(), Placa.of(placa),
                "Toyota", "Corolla", 2020, "Prata");
    }
}
