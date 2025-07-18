CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'カテゴリID（主キー）',
    name VARCHAR(50) NOT NULL COMMENT 'カテゴリ名（エンジン、ブレーキ等）',
    display_order INT COMMENT '表示順序',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',

    CONSTRAINT uk_categories_name UNIQUE (name)
);
COMMENT ON TABLE categories IS '整備カテゴリテーブル';

INSERT INTO categories (name, display_order) VALUES
    ('エンジン', 1),
    ('ブレーキ', 2),
    ('サスペンション', 3),
    ('タイヤ・ホイール', 4),
    ('マフラー', 5),
    ('エアクリーナー', 6),
    ('電装系', 7),
    ('チェーン・スプロケット', 8),
    ('クラッチ', 9),
    ('ハンドル', 10),
    ('ミラー', 11),
    ('ライト・ランプ', 12),
    ('メーター', 13),
    ('シート', 14),
    ('カウル・外装', 15),
    ('バッテリー', 16),
    ('オイル・ケミカル', 17),
    ('工具', 18),
    ('ヘルメット', 19),
    ('プロテクター', 20),
    ('グローブ', 21),
    ('ウェア', 22),
    ('バッグ・ケース', 23),
    ('アクセサリー', 24),
    ('メンテナンス用品', 25);