CREATE TABLE transaction_users (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    estabelecimento VARCHAR(255),
    categoria VARCHAR(255),
    metodo_pagamento VARCHAR(64),
    valor BIGINT NOT NULL,
    computada BOOLEAN NOT NULL DEFAULT FALSE,
    feito_em TIMESTAMP WITHOUT TIME ZONE,
    mes_parcela INT NOT NULL DEFAULT 1,
    parcelas_totais INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_transaction_users_user FOREIGN KEY (user_id) REFERENCES vault_users (id_user) ON DELETE CASCADE
);

CREATE INDEX idx_transaction_users_user_id ON transaction_users USING btree (user_id);
CREATE INDEX idx_transaction_users_computada ON transaction_users USING btree (computada);