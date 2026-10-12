package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.CargoNotFoundException;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.CargoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.FuncionarioPersistenceMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CargoRepositoryImplTest {

    @Mock
    private CargoJpaRepository jpaRepository;

    @Mock
    private FuncionarioPersistenceMapper mapper;

    @InjectMocks
    private CargoRepositoryImpl repository;

    private CargoEntity cargoEntity() {
        CargoEntity entity = new CargoEntity();
        entity.setId(1L);
        entity.setNome("MECANICO");
        entity.setRoles(Set.of());
        return entity;
    }

    private Cargo cargoDomain() {
        return new Cargo(1L, "MECANICO", Set.of());
    }

    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("deve retornar Optional com Cargo quando encontrado")
        void shouldReturnCargoWhenFound() {
            CargoEntity entity = cargoEntity();
            when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(cargoDomain());

            Optional<Cargo> result = repository.findById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            assertThat(result.get().getNome()).isEqualTo("MECANICO");
        }

        @Test
        @DisplayName("deve retornar Optional vazio quando não encontrado")
        void shouldReturnEmptyWhenNotFound() {
            when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

            Optional<Cargo> result = repository.findById(99L);

            assertThat(result).isEmpty();
            verify(mapper, never()).toDomain(any(CargoEntity.class));
        }
    }

    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("deve retornar Cargo quando encontrado")
        void shouldReturnCargoWhenFound() {
            CargoEntity entity = cargoEntity();
            when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(cargoDomain());

            Cargo result = repository.getById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getNome()).isEqualTo("MECANICO");
        }

        @Test
        @DisplayName("deve lançar CargoNotFoundException quando não encontrado")
        void shouldThrowWhenNotFound() {
            when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> repository.getById(99L))
                    .isInstanceOf(CargoNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getByIdWithRoles")
    class GetByIdWithRoles {

        @Test
        @DisplayName("deve retornar Cargo com roles quando encontrado")
        void shouldReturnCargoWithRolesWhenFound() {
            CargoEntity entity = cargoEntity();
            when(jpaRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(cargoDomain());

            Cargo result = repository.getByIdWithRoles(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getNome()).isEqualTo("MECANICO");
            verify(jpaRepository).findByIdWithRoles(1L);
        }

        @Test
        @DisplayName("deve lançar CargoNotFoundException quando não encontrado")
        void shouldThrowWhenNotFound() {
            when(jpaRepository.findByIdWithRoles(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> repository.getByIdWithRoles(99L))
                    .isInstanceOf(CargoNotFoundException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("não deve usar findById ao chamar getByIdWithRoles")
        void shouldNotDelegatToFindById() {
            when(jpaRepository.findByIdWithRoles(1L)).thenReturn(Optional.of(cargoEntity()));
            when(mapper.toDomain(any(CargoEntity.class))).thenReturn(cargoDomain());

            repository.getByIdWithRoles(1L);

            verify(jpaRepository, never()).findById(any());
        }
    }
}
