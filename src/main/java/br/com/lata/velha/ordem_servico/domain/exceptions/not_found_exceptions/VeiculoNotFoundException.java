package br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.value_objects.Placa;
import br.com.lata.velha.shared.domain.exceptions.NotFoundException;

public class VeiculoNotFoundException extends NotFoundException {
    private VeiculoNotFoundException(String param, String value) {
        super(Veiculo.class, param, value);
    }

    public static VeiculoNotFoundException fromId(Long value) {
        return new VeiculoNotFoundException("id", value.toString());
    }

    public static VeiculoNotFoundException fromPlaca(Placa placa) {
        return new VeiculoNotFoundException("placa", placa.toString());
    }
}