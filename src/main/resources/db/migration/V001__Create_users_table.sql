CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ユーザーID（主キー）',
    nickname VARCHAR(50) NOT NULL COMMENT 'ユーザーのニックネーム',
    display_email VARCHAR(255) COMMENT '外部認証から取得したメールアドレス（表示用のみ）',
    unique_user_id VARCHAR(255) NOT NULL UNIQUE COMMENT '外部認証システムから取得した一意なユーザーID',
    roles VARCHAR(100) NOT NULL DEFAULT 'USER' COMMENT 'ユーザーの権限情報',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '論理削除フラグ',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時'
);

CREATE INDEX idx_users_unique_user_id ON users(unique_user_id);
CREATE INDEX idx_users_is_deleted ON users(is_deleted);