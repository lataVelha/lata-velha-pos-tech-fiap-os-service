package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.ProprietarioNotFoundException;
import br.com.lata.velha.ordem_servico.domain.value_objects.Documento;
import br.com.lata.velha.ordem_servico.domain.value_objects.Endereco;
import br.com.lata.velha.ordem_servico.domain.value_objects.NumeroCelular;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.ProprietarioPersistenceMapper;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import br.com.lata.velha.shared.infrastructure.logging.Slf4jLogger;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
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
@Import({ProprietarioRepositoryImpl.class, ProprietarioPersistenceMapper.class, Slf4jLogger.class})
class ProprietarioRepositoryImplTest {

    @Autowired
    private ProprietarioRepositoryImpl repository;

    private Proprietario proprietario;

    @BeforeEach
    void setUp() {
        proprietario = buildProprietario(null, "11144477735", "joao@email.com");
    }

    @Test
    @DisplayName("deve salvar novo proprietário com sucesso")
    void shouldSaveNewProprietario() {
        Proprietario saved = repository.save(proprietario);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNome()).isEqualTo("João Silva");
        assertThat(saved.getDocumento().getValor()).isEqualTo("11144477735");
        assertThat(saved.getEmail()).isEqualTo("joao@email.com");
        assertThat(saved.isAtivo()).isTrue();
    }

    @Test
    @DisplayName("deve salvar proprietário com endereço")
    void shouldSaveProprietarioWithEndereco() {
        Endereco endereco = new Endereco("Rua das Flores", "01310100", "42");
        Proprietario comEndereco = new Proprietario(null, "João Silva", "joao@email.com",
                Documento.of("11144477735"), NumeroCelular.of("11987654321"), endereco);

        Proprietario saved = repository.save(comEndereco);

        assertThat(saved.getEndereco()).isNotNull();
        assertThat(saved.getEndereco().getRua()).isEqualTo("Rua das Flores");
        assertThat(saved.getEndereco().getCep()).isEqualTo("01310100");
        assertThat(saved.getEndereco().getNumeroCasa()).isEqualTo("42");
    }

    @Test
    @DisplayName("deve lançar exceção ao salvar proprietário com documento duplicado")
    void shouldThrowWhenDocumentoDuplicated() {
        repository.save(proprietario);

        Proprietario duplicate = buildProprietario(null, "11144477735", "outro@email.com");

        assertThatThrownBy(() -> repository.save(duplicate))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    @DisplayName("deve atualizar proprietário existente sem validar documento duplicado")
    void shouldUpdateExistingProprietarioWithoutDocumentoCheck() {
        Proprietario saved = repository.save(proprietario);
        saved.setNome("João Atualizado");

        Proprietario updated = repository.save(saved);

        assertThat(updated.getNome()).isEqualTo("João Atualizado");
        assertThat(updated.getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("deve encontrar proprietário ativo por id")
    void shouldGetActiveById() {
        Proprietario saved = repository.save(proprietario);

        Proprietario found = repository.getActiveById(saved.getId());

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.isAtivo()).isTrue();
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar proprietário ativo por id inexistente")
    void shouldThrowWhenGetActiveByIdNotFound() {
        assertThatThrownBy(() -> repository.getActiveById(999L))
                .isInstanceOf(ProprietarioNotFoundException.class);
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar ativo por id de proprietário inativo")
    void shouldThrowWhenGetActiveByIdButProprietarioIsInactive() {
        Proprietario saved = repository.save(proprietario);
        saved.deactivate();
        repository.save(saved);
        var id = saved.getId();

        assertThatThrownBy(() -> repository.getActiveById(id))
                .isInstanceOf(ProprietarioNotFoundException.class);
    }

    @Test
    @DisplayName("deve encontrar proprietário ativo por documento")
    void shouldFindActiveByDocumento() {
        repository.save(proprietario);

        Proprietario found = repository.findActiveByDocumento("11144477735");

        assertThat(found.getDocumento().getValor()).isEqualTo("11144477735");
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar por documento inexistente")
    void shouldThrowWhenFindActiveByDocumentoNotFound() {
        assertThatThrownBy(() -> repository.findActiveByDocumento("11144477735"))
                .isInstanceOf(ProprietarioNotFoundException.class);
    }

    @Test
    @DisplayName("deve encontrar proprietário inativo por id")
    void shouldFindInactiveById() {
        Proprietario saved = repository.save(proprietario);
        saved.deactivate();
        repository.save(saved);

        Proprietario found = repository.findInactiveById(saved.getId());

        assertThat(found.isAtivo()).isFalse();
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar proprietário inativo por id inexistente")
    void shouldThrowWhenFindInactiveByIdNotFound() {
        assertThatThrownBy(() -> repository.findInactiveById(999L))
                .isInstanceOf(ProprietarioNotFoundException.class);
    }

    @Test
    @DisplayName("deve listar apenas proprietários ativos")
    void shouldFindAllActive() {
        repository.save(proprietario);

        Proprietario inativo = buildProprietario(null, "52998224725", "inativo@email.com");
        Proprietario savedInativo = repository.save(inativo);
        savedInativo.deactivate();
        repository.save(savedInativo);

        List<Proprietario> ativos = repository.findAllActive();

        assertThat(ativos).isNotEmpty()
                .allMatch(Proprietario::isAtivo);
    }

    @Test
    @DisplayName("deve retornar resultado paginado de proprietários ativos")
    void shouldFindAllActivePaginated() {
        repository.save(buildProprietario(null, "11144477735", "p1@email.com"));
        repository.save(buildProprietario(null, "52998224725", "p2@email.com"));

        PaginatedResult<Proprietario> result = repository.findAllActivePaginated(0, 10);

        assertThat(result.content()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isGreaterThanOrEqualTo(2);
        assertThat(result.totalPages()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("deve retornar true quando documento já existe")
    void shouldReturnTrueWhenDocumentoExists() {
        repository.save(proprietario);

        assertThat(repository.existsByDocumento("11144477735")).isTrue();
    }

    @Test
    @DisplayName("deve retornar false quando documento não existe")
    void shouldReturnFalseWhenDocumentoNotExists() {
        assertThat(repository.existsByDocumento("11144477735")).isFalse();
    }

    private Proprietario buildProprietario(Long id, String documento, String email) {
        return new Proprietario(id, "João Silva", email,
                Documento.of(documento), NumeroCelular.of("11987654321"), null);
    }
}
