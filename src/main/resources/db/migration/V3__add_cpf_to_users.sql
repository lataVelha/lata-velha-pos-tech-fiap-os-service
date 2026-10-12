-- CPF passa a ser um segundo fator de identificacao do USER, usado pela
-- function serverless de autenticacao (lambda + API Gateway) para emitir
-- um JWT sem depender de email/senha. O login por email/senha (LoginController)
-- continua funcionando normalmente. CPF e obrigatorio para todo USER.

ALTER TABLE USERS ADD COLUMN CPF VARCHAR(11) UNIQUE;

-- CPFs de teste (validos pelo algoritmo de digito verificador) para os
-- usuarios seed, permitindo exercitar o novo fluxo sem cadastro adicional.
-- Precisam existir antes do NOT NULL abaixo.
UPDATE USERS SET CPF = '11144477735' WHERE EMAIL = 'admin@latavelha.com';
UPDATE USERS SET CPF = '22255588846' WHERE EMAIL = 'atendente@latavelha.com';
UPDATE USERS SET CPF = '33366699957' WHERE EMAIL = 'mecanico@latavelha.com';

CREATE TEMP SEQUENCE IF NOT EXISTS cpf_fallback_seq START 99999999900;
UPDATE USERS SET CPF = LPAD(NEXTVAL('cpf_fallback_seq')::TEXT, 11, '0') WHERE CPF IS NULL;

ALTER TABLE USERS ALTER COLUMN CPF SET NOT NULL;
