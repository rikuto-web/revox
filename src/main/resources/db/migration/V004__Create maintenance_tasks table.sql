CREATE TABLE maintenance_tasks (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'メンテナンスタスクID（主キー）',
    category_id INT NOT NULL COMMENT 'カテゴリID（外部キー）',
    name VARCHAR(100) NOT NULL COMMENT 'タスク名',
    ai_guidance TEXT NOT NULL COMMENT 'AIが生成したメンテナンス手順・必要物品情報',
     is_deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '論理削除フラグ
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',

    CONSTRAINT fk_maintenance_tasks_category_id
        FOREIGN KEY (category_id) REFERENCES categories(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);
COMMENT ON TABLE maintenance_tasks IS 'メンテナンスタスクテーブル（AI生成情報保存用）';