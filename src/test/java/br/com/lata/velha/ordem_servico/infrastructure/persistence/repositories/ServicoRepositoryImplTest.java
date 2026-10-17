package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.ServicoNotFoundException;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ServicoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.ServicoPersistenceMapper;
import br.com.lata.velha.shared.application.logging.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoRepositoryImplTest {

    @Mock
    private ServicoJpaRepository jpaRepository;

    @Mock
    private ServicoPersistenceMapper mapper;

    @Mock
    private Logger logger;

    @InjectMocks
    private ServicoRepositoryImpl repository;

    private Servico domainServico() {
        return new Servico(1L, "Troca de óleo", "Substituição do óleo do motor");
    }

    private ServicoEntity entityServico() {
        var e = new ServicoEntity();
        e.setId(1L);
        e.setNome("Troca de óleo");
        e.setDescricao("Substituição do óleo do motor");
        e.setAtivo(true);
        return e;
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("deve salvar e retornar domínio mapeado")
        void deveSalvarERetornarDominio() {
            ServicoEntity entity = entityServico();
            when(mapper.toEntity(any())).thenReturn(entity);
            when(jpaRepository.save(entity)).thenReturn(entity);
            when(mapper.toDomain(entity)).thenReturn(domainServico());

            Servico result = repository.save(domainServico());

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getNome()).isEqualTo("Troca de óleo");
            verify(jpaRepository).save(entity);
        }
    }

    @Nested
    @DisplayName("findActiveById")
    class FindActiveById {

        @Test
        @DisplayName("deve retornar domínio quando encontrado")
        void deveRetornarQuandoEncontrado() {
            ServicoEntity entity = entityServico();
            when(jpaRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(domainServico());

            Servico result = repository.getActiveById(1L);

            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("deve lançar ServicoNotFoundException quando não encontrado")
        void deveLancarExcecaoQuandoNaoEncontrado() {
            when(jpaRepository.findByIdAndAtivoTrue(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> repository.getActiveById(99L))
                    .isInstanceOf(ServicoNotFoundException.class)
                    .hasMessageContaining("99");

            verify(mapper, never()).toDomain(any());
        }
    }

    @Nested
    @DisplayName("findAllActivePaginated")
    class FindAllActivePaginated {

        @Test
        @DisplayName("deve retornar PaginatedResult com registros")
        void deveRetornarPaginatedResult() {
            ServicoEntity entity = entityServico();
            var page = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);
            when(jpaRepository.findByAtivoTrue(any())).thenReturn(page);
            when(mapper.toDomain(entity)).thenReturn(domainServico());

            var result = repository.findAllActivePaginated(0, 10);

            assertThat(result.content()).hasSize(1);
            assertThat(result.totalElements()).isEqualTo(1);
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("deve retornar PaginatedResult vazio quando não há registros")
        void deveRetornarVazioQuandoSemRegistros() {
            var page = new PageImpl<ServicoEntity>(List.of(), PageRequest.of(0, 10), 0);
            when(jpaRepository.findByAtivoTrue(any())).thenReturn(page);

            var result = repository.findAllActivePaginated(0, 10);

            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isZero();
        }
    }
}
