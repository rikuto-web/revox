CREATE TABLE ai_questions (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    bike_id INT NOT NULL,
    category_id INT NOT NULL,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE ai_questions ADD CONSTRAINT fk_ai_questions_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE ai_questions ADD CONSTRAINT fk_ai_questions_bike_id FOREIGN KEY (bike_id) REFERENCES bikes(id) ON DELETE CASCADE;
ALTER TABLE ai_questions ADD CONSTRAINT fk_ai_questions_category_id FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE;

CREATE INDEX idx_ai_questions_user_id ON ai_questions(user_id);
CREATE INDEX idx_ai_questions_bike_id ON ai_questions(bike_id);

CREATE TRIGGER update_ai_questions_updated_at
BEFORE UPDATE ON ai_questions
FOR EACH ROW
EXECUTE FUNCTION set_updated_at_to_now();