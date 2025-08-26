CREATE TABLE bikes (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    manufacturer VARCHAR(50) NOT NULL,
    model_name VARCHAR(100) NOT NULL,
    model_code VARCHAR(20),
    model_year INT,
    current_mileage INT,
    purchase_date DATE,
    image_url VARCHAR(2048),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE bikes ADD CONSTRAINT fk_bikes_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

CREATE TRIGGER update_bikes_updated_at
BEFORE UPDATE ON bikes
FOR EACH ROW
EXECUTE FUNCTION set_updated_at_to_now();