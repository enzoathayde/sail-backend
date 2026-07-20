CREATE TABLE vault_users (
    id_user SERIAL PRIMARY KEY,
    user_name VARCHAR(64) NOT NULL,
    vault_key TEXT NOT NULL,
    vault_key_fingerprint CHAR(64) NOT NULL
);

CREATE INDEX idx_vault_users_user_name ON vault_users USING btree (user_name);
CREATE UNIQUE INDEX ux_vault_users_vault_key_fingerprint ON vault_users USING btree (vault_key_fingerprint);