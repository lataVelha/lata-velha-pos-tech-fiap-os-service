package br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.value_objects.Placa;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ProprietarioEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.VeiculoEntity;
import org.springframework.stereotype.Component;

@Component
public class VeiculoPersistenceMapper {

    public Veiculo toDomain(VeiculoEntity entity) {
        if (entity == null) return null;

        Veiculo veiculo = new Veiculo(
                entity.getId(),
                entity.getProprietario().getId(),
                Placa.of(entity.getPlaca()),
                entity.getMarca(),
                entity.getModelo(),
                entity.getAno(),
                entity.getCor()
        );
        veiculo.setAtivo(entity.isAtivo());
        return veiculo;
    }

    public VeiculoEntity toEntity(Veiculo domain, ProprietarioEntity proprietarioEntity) {
        if (domain == null) return null;

        VeiculoEntity entity = new VeiculoEntity();
        entity.setId(domain.getId());
        entity.setProprietario(proprietarioEntity);
        entity.setPlaca(domain.getPlaca().getValor());
        entity.setMarca(domain.getMarca());
        entity.setModelo(domain.getModelo());
        entity.setAno(domain.getAno());
        entity.setCor(domain.getCor());
        entity.setAtivo(domain.isAtivo());
        return entity;
    }
}