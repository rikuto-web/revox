CREATE TABLE service_records (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'サービス記録ID（主キー）',
    user_id INT NOT NULL COMMENT 'ユーザーID（外部キー）',
    bike_id INT NOT NULL COMMENT 'バイクID（外部キー）',
    maintenance_task_id INT NOT NULL COMMENT 'メンテナンスタスクID（外部キー）',
    ai_advice_original TEXT COMMENT 'AIが生成したオリジナルアドバイス',
    user_edited_content TEXT NOT NULL COMMENT 'ユーザーが編集したコンテンツ',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',

    CONSTRAINT fk_service_records_user_id
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT fk_service_records_bike_id
        FOREIGN KEY (bike_id) REFERENCES bikes(id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT fk_service_records_maintenance_task_id
        FOREIGN KEY (maintenance_task_id) REFERENCES maintenance_tasks(id)
        ON DELETE CASCADE ON UPDATE CASCADE
);
COMMENT ON TABLE service_records IS 'サービス記録テーブル（AIアドバイス＋ユーザー編集内容）';

CREATE INDEX idx_service_records_user_id ON service_records(user_id);