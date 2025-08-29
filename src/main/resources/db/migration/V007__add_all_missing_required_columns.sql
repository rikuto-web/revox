-- V6（ダミーデータ）で不足していた必須項目の追加

-- users テーブルの変更
ALTER TABLE users ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE users ADD COLUMN created_at TIMESTAMP WITH TIME ZONE NOT NULL;
ALTER TABLE users ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE NOT NULL;

UPDATE users SET created_at = NOW(), updated_at = NOW() WHERE created_at IS NULL;

-- bikes テーブルの変更
ALTER TABLE bikes ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE bikes ADD COLUMN created_at TIMESTAMP WITH TIME ZONE NOT NULL;
ALTER TABLE bikes ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE NOT NULL;

UPDATE bikes SET created_at = NOW(), updated_at = NOW() WHERE created_at IS NULL;

-- ai_questions テーブルの変更
ALTER TABLE ai_questions ADD COLUMN created_at TIMESTAMP WITH TIME ZONE NOT NULL;
ALTER TABLE ai_questions ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE NOT NULL;

UPDATE ai_questions SET created_at = NOW(), updated_at = NOW() WHERE created_at IS NULL;

-- maintenance_tasks テーブルの変更
ALTER TABLE maintenance_tasks ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE maintenance_tasks ADD COLUMN created_at TIMESTAMP WITH TIME ZONE NOT NULL;
ALTER TABLE maintenance_tasks ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE NOT NULL;

UPDATE maintenance_tasks SET created_at = NOW(), updated_at = NOW() WHERE created_at IS NULL;