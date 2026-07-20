CREATE TABLE chat_messages (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    sender VARCHAR(16) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE INDEX idx_chat_messages_user_id ON chat_messages USING btree (user_id);
