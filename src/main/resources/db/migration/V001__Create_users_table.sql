CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ユーザーID（主キー）',
    nickname VARCHAR(50) NOT NULL COMMENT 'ユーザーのニックネーム',
    email VARCHAR(255) COMMENT 'メールアドレス',
    google_id VARCHAR(100) COMMENT 'Google認証ID（OAuth用）',
    line_id VARCHAR(100) COMMENT 'LINE認証ID（OAuth用）',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',

    CONSTRAINT uk_users_google_id UNIQUE (google_id),
    CONSTRAINT uk_users_line_id UNIQUE (line_id)
);
COMMENT ON TABLE users IS 'ユーザー情報テーブル';

CREATE INDEX idx_users_google_id ON users(google_id);
CREATE INDEX idx_users_line_id ON users(line_id);