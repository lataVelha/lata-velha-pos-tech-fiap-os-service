CREATE TABLE historico_estado_os (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    os_id BIGINT NOT NULL,
    estado_os VARCHAR(50) NOT NULL,
    data_inicio TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    data_fim TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT pk_historico_estado_os PRIMARY KEY (id),
    CONSTRAINT fk_historico_os FOREIGN KEY (os_id) REFERENCES ordem_servico (id) ON DELETE CASCADE
);

CREATE INDEX idx_historico_os_id ON historico_estado_os(os_id);

CREATE INDEX idx_historico_data_inicio ON historico_estado_os(data_inicio);
