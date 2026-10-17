package br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions;

import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.shared.domain.exceptions.NotFoundException;

public class CargoNotFoundException extends NotFoundException {
    private CargoNotFoundException(String paramName, String paramValue) {
        super(Cargo.class, paramName, paramValue);
    }

    public static CargoNotFoundException fromId(Long id) {
        return new CargoNotFoundException("id", id.toString());
    }
}
