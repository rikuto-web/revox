CREATE TABLE bikes (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'バイクID（主キー）',
    user_id INT NOT NULL COMMENT '所有者のユーザーID',
    manufacturer VARCHAR(50) NOT NULL COMMENT 'メーカー名（Honda、Yamaha等）',
    model_name VARCHAR(100) NOT NULL COMMENT 'モデル名（CBR1000RR等）',
    model_code VARCHAR(20) COMMENT 'モデルコード（型式番号）',
    model_year INT COMMENT '年式',
    current_mileage INT COMMENT '現在の走行距離（km）',
    purchase_date DATE COMMENT '購入日',
    image_url VARCHAR(2048) COMMENT 'バイク画像のURL',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',

    CONSTRAINT fk_bikes_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
COMMENT ON TABLE bikes IS 'バイク情報テーブル';