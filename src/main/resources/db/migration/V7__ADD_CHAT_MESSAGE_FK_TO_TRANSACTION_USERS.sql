ALTER TABLE transaction_users
    ADD COLUMN chat_message_id BIGINT,
    ADD CONSTRAINT fk_transaction_users_chat_message
        FOREIGN KEY (chat_message_id) REFERENCES chat_messages(id),
    ADD CONSTRAINT uq_transaction_users_chat_message
        UNIQUE (chat_message_id);
