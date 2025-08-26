CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    display_order INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER update_categories_updated_at
BEFORE UPDATE ON categories
FOR EACH ROW
EXECUTE FUNCTION set_updated_at_to_now();

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