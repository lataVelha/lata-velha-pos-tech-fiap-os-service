package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.FuncionarioNotFoundException;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.FuncionarioEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.FuncionarioPersistenceMapper;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FuncionarioRepositoryImplTest {

    @Mock
    private FuncionarioJpaRepository jpaRepository;

    @Mock
    private FuncionarioPersistenceMapper mapper;

    @Mock
    private Logger logger;

    @InjectMocks
    private FuncionarioRepositoryImpl repository;

    @Test
    @DisplayName("deve encontrar funcionário por id existente")
    void shouldFindById() {
        UUID userId = UUID.randomUUID();
        FuncionarioEntity entity = new FuncionarioEntity();
        entity.setId(1L);
        entity.setNome("Fiap");
        entity.setUserId(userId);

        Funcionario funcionario = new Funcionario(1L, "Fiap", new Cargo(1L, "ADMIN", null), UserId.create(userId));

        when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(funcionario);

        Optional<Funcionario> result = repository.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getNome()).isEqualTo("Fiap");
    }

    @Test
    @DisplayName("deve retornar Optional vazio quando funcionário não existe")
    void shouldReturnEmptyWhenNotFound() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Funcionario> result = repository.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("deve retornar funcionário ao usar getById com id existente")
    void shouldGetById() {
        UUID userId = UUID.randomUUID();
        FuncionarioEntity entity = new FuncionarioEntity();
        entity.setId(1L);
        entity.setNome("Fiap");
        entity.setUserId(userId);

        Funcionario funcionario = new Funcionario(1L, "Fiap", new Cargo(1L, "ADMIN", null), UserId.create(userId));

        when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(funcionario);

        Funcionario result = repository.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo("Fiap");
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar id inexistente com getById")
    void shouldThrowWhenUsernameNotFound() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> repository.getById(99L))
                .isInstanceOf(FuncionarioNotFoundException.class);
    }

    @Test
    @DisplayName("deve retornar true quando funcionário com userId existe")
    void shouldReturnTrueWhenExistsByUserId() {
        UUID userId = UUID.randomUUID();
        when(jpaRepository.existsByUserId(userId)).thenReturn(true);

        boolean result = repository.existsByUserId(UserId.create(userId));

        assertThat(result).isTrue();
        verify(jpaRepository).existsByUserId(userId);
    }

    @Test
    @DisplayName("deve retornar false quando funcionário com userId não existe")
    void shouldReturnFalseWhenNotExistsByUserId() {
        UUID userId = UUID.randomUUID();
        when(jpaRepository.existsByUserId(userId)).thenReturn(false);

        boolean result = repository.existsByUserId(UserId.create(userId));

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("deve salvar e retornar o funcionário mapeado")
    void shouldSaveFuncionario() {
        UUID userId = UUID.randomUUID();
        Funcionario funcionario = new Funcionario(null, "Carlos", new Cargo(1L, "MECANICO", null), UserId.create(userId));
        FuncionarioEntity entity = new FuncionarioEntity();
        entity.setNome("Carlos");
        entity.setUserId(userId);

        Funcionario saved = new Funcionario(10L, "Carlos", new Cargo(1L, "MECANICO", null), UserId.create(userId));

        when(mapper.toEntity(funcionario)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(saved);

        Funcionario result = repository.save(funcionario);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getNome()).isEqualTo("Carlos");
        verify(jpaRepository).save(entity);
    }
}
