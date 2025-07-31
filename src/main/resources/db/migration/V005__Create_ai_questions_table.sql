CREATE TABLE ai_questions (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'AI質問ID（主キー）',
    user_id INT NOT NULL COMMENT 'ユーザーID（外部キー）',
    bike_id INT NOT NULL COMMENT 'バイクID（外部キー）',
    category_id INT NOT NULL COMMENT 'カテゴリID（外部キー）',
    question TEXT NOT NULL COMMENT 'ユーザーが入力した質問内容',
    answer TEXT NOT NULL COMMENT 'AIが生成した回答内容',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '論理削除フラグ',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
    CONSTRAINT fk_ai_questions_user_id
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ai_questions_bike_id
        FOREIGN KEY (bike_id) REFERENCES bikes(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ai_questions_category_id
        FOREIGN KEY (category_id) REFERENCES categories(id)
        ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX idx_ai_questions_user_id ON ai_questions(user_id);
CREATE INDEX idx_ai_questions_bike_id ON ai_questions(bike_id);